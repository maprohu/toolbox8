package toolbox8.jartree.client

import java.io.File

import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.{ActorMaterializer, Attributes, Materializer}
import akka.stream.scaladsl.{FileIO, Flow, Keep, Sink, Source, Tcp}
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import maven.modules.builder.{HasMavenCoordinates, ModulePath, NamedModule}
import toolbox6.jartree.api.{ClassRequest, JarPlugger, JarSeq}
import toolbox6.jartree.packaging.JarTreePackaging
import toolbox8.akka.statemachine.AkkaStreamCoding.StateMachine
import toolbox8.akka.statemachine.{AkkaStreamCoding, DeepStream}
import toolbox8.akka.stream.AkkaStreamTools
import toolbox8.akka.stream.AkkaStreamTools.Components
import toolbox8.jartree.extra.client.ExecClient
import toolbox8.jartree.extra.shared.ExecProtocol.Executable
import toolbox8.jartree.protocol.JarTreeStandaloneProtocol.Management
import toolbox8.jartree.protocol.JarTreeStandaloneProtocol.Management._
import toolbox8.jartree.standaloneapi.{JarTreeStandaloneContext, Protocol, Service}

import scala.concurrent.{Await, Future, Promise}
import scala.collection.immutable._

/**
  * Created by martonpapp on 16/10/16.
  */
object JarTreeStandaloneClient extends LazyLogging {

  type ByteFlow[Mat] = Flow[ByteString, ByteString, Mat]

  val EmptyFlow : ByteFlow[NotUsed] =
    Flow.fromSinkAndSource(
      Sink.ignore,
      Source.maybe
    )

  case class Flows[MatM, MatD](
    management: ByteFlow[MatM],
    data: ByteFlow[MatD]
  )

  case class Endpoint(
    host: String,
    port: Int
  )

  import boopickle.Default._
  import AkkaStreamCoding.Implicits._
  import AkkaStreamCoding.StateMachine.State

  def target(
    host: String,
    port: Int = Protocol.DefaultPort
  ) = {
    new Targeting(
      Endpoint(
        host,
        port
      )
    )
  }

  class Targeting(
    endpoint: Endpoint
  ) {


    def run(
      flows: Components => Flows[_, _]
    ) = {

      val cmp = AkkaStreamTools.Info
      import cmp._

      val peer =
        Tcp()
          .outgoingConnection(
            endpoint.host,
            endpoint.port
          )

      val f = flows(cmp)
      import f._

      val dataFlow =
        Flow[ByteString]
          .log("data-in")
          .via(
            data
          )

      peer
        .log("peer-in")
        .join(
          AkkaStreamCoding.framing.reversed
        )
        .join(
          AkkaStreamCoding.Multiplex.flow(
            management,
            dataFlow
          )
        )
        .run()
        .onComplete(println)

    }

    def runExec[Ctx](
      module: NamedModule,
      runClassName: String,
      target: ModulePath,
      runWith: Flow[ByteString, ByteString, _],
      dataTrf: ByteFlow[NotUsed] => ByteFlow[NotUsed] = identity
    ) = {
      run(
        { implicit mat =>
          import mat._
          val classPath =
            module
              .asModule
              .forTarget(
                target
              )
              .classPath

          val promise = Promise[Option[Unit]]()

          val management =
            managementFlow(
              upload(
                classPath,
                { () =>
                  logger.info("signaling upload done")
                  promise.success(Some())
                  StateMachine.End
                }
            )
          )

          val data =
            ExecClient
              .flow(
                JarTreePackaging.request[Executable[Ctx]](
                  classPath,
                  runClassName
                ),
                runWith
              )
              .mapMaterializedValue({ p =>
                p.completeWith(promise.future)
                NotUsed
              })

          Flows(
            management,
            dataTrf(data)
          )
        }
      )
    }

    def runPlug(
      module: NamedModule,
      runClassName: String,
      target: NamedModule
    ) = {
      runManagement(
        { implicit mat =>
          import mat._
          val rmh =
            module
              .asModule
              .forTarget(
                ModulePath(
                  target,
                  None
                )
              )

          upload(
            rmh
              .classPath,
            () => plug(
              ClassRequest[Plugger](
                JarSeq(
                  rmh
                    .classPath
                    .map(JarTreePackaging.getId)
                ),
                runClassName
              )
            )

          )
        }
      )
    }

    def runCat(
      sink: Components => Sink[ByteString, _]
    ) = {
      runData(
        mat => Flow.fromSinkAndSource(
          sink(mat),
          Source.maybe
        )
      )
    }

    def runData[M](
      flow: Components => ByteFlow[M]
    ) = {
      run(
        mat => Flows(
          management = EmptyFlow,
          data = flow(mat)
        )
      )
    }


    def runQuery(
    ) = {
      runManagement(
        { implicit mat =>
          import mat._
          import actorSystem.dispatcher

          val bytes =
            Pickle
              .intoBytes[Starter](
              Query
            )
              .asByteString

          logger.info("sending query: {}", bytes)

          State(
            out =
              Source.single(
                Source.single(
                  bytes
                )
              ),
            next = { d =>
              AkkaStreamCoding
                .unpickle[QueryResponse](d)
                .map({ r =>
                  println(r)
                  StateMachine.End
                })
            }
          )
        }
      )
    }

    def runManagement(
      state: Components => State
    ) = {
      run(
        { mat =>
          val management : ByteFlow[NotUsed] = managementFlow(
            state(mat)
          )
          Flows(
            management,
            EmptyFlow
          )
        }
      )
    }

  }





  def managementFlow(
    state: State
  ) : ByteFlow[NotUsed] = {
    AkkaStreamCoding
      .Terminal
      .bidi
      .join(
        AkkaStreamCoding
          .StateMachine
          .flow(
            state
          )
      )
  }


  def upload(
    rmh: Seq[HasMavenCoordinates],
    andThen: () => State
  )(implicit
    materializer: Materializer
  ) : State = {

    logger.info(s"updating to: ${rmh.toString}")
    val jars = JarTreePackaging.resolverJarsFile(rmh)
    logger.info(s"required jars: ${jars.map(_._1).mkString(", ")}")

    val first =
      Source.single(
        Source.single(
          Pickle
            .intoBytes[Starter](
              VerifyRequest(
                ids = jars.map(_._1)
              )
            )
            .asByteString
        )
      )

    State(
      out = first,
      next = verifying(jars, andThen)
    )
  }

  def verifying(
    files: IndexedSeq[(String, File)],
    andThen: () => State
  )(
    data: AkkaStreamCoding.Data
  )(implicit
    materializer: Materializer
  ) : Future[State] = {
    import materializer.executionContext

    val mfilesF =
      AkkaStreamCoding
        .unpickle[VerifyResponse](data)
        .map({ res =>
            val (ids, fs) =
              res
                .missing
                .map(files)
                .unzip

          logger.info(s"missing jars: ${ids.mkString(", ")}")
          fs
        })

    val out =
      Source
        .fromFuture(
          mfilesF
        )
        .mapConcat({ mfiles =>
          logger.info(s"uploading files: ${mfiles.mkString(", ")}")
          mfiles
            .map({ elem =>
              FileIO.fromPath(elem.toPath)
            })
        })

    Future.successful(
      State(
        out,
        { data =>
          AkkaStreamCoding
            .unpickle[Management.Done.type ](data)
            .map({ done =>
              logger.info(s"upload done: ${done}")
              andThen()
            })
        }
      )
    )
  }

  def plug(
    classRequest: ClassRequest[JarPlugger[Service, JarTreeStandaloneContext]]
  )(implicit
    materializer: Materializer
  ) : State = {
    import materializer.executionContext
    State(
      out =
        Source.single(
          AkkaStreamCoding.pickle[Starter](
            Plug(
              classRequest
            )
          )
        ),
      { data =>
        AkkaStreamCoding
          .unpickle[Management.Done.type ](data)
          .map({ done =>
            logger.info(s"plug done: ${done}")
            StateMachine.End
          })
      }

    )

  }

}
