package toolbox8.jartree.client

import java.io.File

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{FileIO, Flow, Keep, Sink, Source, Tcp}
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import maven.modules.builder.NamedModule
import toolbox6.jartree.packaging.JarTreePackaging
import toolbox6.jartree.packaging.JarTreePackaging.{RunHierarchy, RunMavenHierarchy}
import toolbox8.akka.statemachine.AkkaStreamCoding.StateMachine
import toolbox8.akka.statemachine.{AkkaStreamCoding, DeepStream}
import toolbox8.jartree.protocol.JarTreeStandaloneProtocol.Management
import toolbox8.jartree.protocol.JarTreeStandaloneProtocol.Management._

import scala.concurrent.{Await, Future}

/**
  * Created by martonpapp on 16/10/16.
  */
object JarTreeStandaloneClient extends LazyLogging {

  type ByteFlow = Flow[ByteString, ByteString, NotUsed]

  case class Flows(
    management: ByteFlow,
    data: ByteFlow
  )

  def run(
    host: String,
    port: Int,
    flows: Materializer => Flows
  ) = {

    implicit val actorSystem = ActorSystem()
    implicit val materializer = ActorMaterializer()
    import actorSystem.dispatcher

    val peer =
      Tcp()
        .outgoingConnection(
          host,
          port
        )

    val f = flows(materializer)
    import f._

    peer
      .join(
        AkkaStreamCoding.framing.reversed
      )
      .join(
        AkkaStreamCoding.Multiplex.flow(
          management,
          data
        )
      )
      .run()
      .onComplete(println)

  }

  def runPlug(
    host: String,
    port: Int,
    runHierarchy: RunHierarchy,
    target: NamedModule
  ) = {
    runManagement(
      host,
      port,
      { implicit mat =>
        start(runHierarchy, target)
      }
    )
  }





  def managementFlow(
    runHierarchy: RunHierarchy,
    target: NamedModule
  )(implicit
    materializer: Materializer
  )  : ByteFlow = {

    AkkaStreamCoding
      .Terminal
      .bidi
      .join(
        AkkaStreamCoding
          .StateMachine
          .flow(
            start(runHierarchy, target)
          )
      )

  }

  import boopickle.Default._
  import AkkaStreamCoding.Implicits._
  import AkkaStreamCoding.StateMachine.State

  def runQuery(
    host: String,
    port: Int
  ) = {
    runManagement(
      host,
      port,
      { implicit mat =>
        import mat.executionContext
        State(
          out =
            Source.single(
              Source.single(
                Pickle
                  .intoByteBuffers(
                    Query
                  )
                  .asByteString
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

  def managementFlow(
    state: State
  ) : ByteFlow = {
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

  def runManagement(
    host: String,
    port: Int,
    state: Materializer => State
  ) = {
    run(
      host,
      port,
      { mat =>
        val management : ByteFlow = managementFlow(
          state(mat)
        )
        val data : ByteFlow =
          Flow.fromSinkAndSource(
            Sink.ignore,
            Source.maybe
          )
        Flows(management, data)
      }
    )
  }

  def start(
    runHierarchy: RunHierarchy,
    target: NamedModule
  )(implicit
    materializer: Materializer
  ) : State = {
    val rmh =
      runHierarchy
        .forTarget(
          JarTreePackaging.target(
            target
          )
        )

    logger.info(s"updating to: ${rmh.toString}")
    val jars = JarTreePackaging.resolverJarsFile(rmh)
    logger.info(s"required jars: ${jars.map(_._1).mkString(", ")}")

    val first =
      Source.single(
        Source.single(
          Pickle
            .intoByteBuffers(
              VerifyRequest(
                ids = jars.map(_._1)
              )
            )
            .asByteString
        )
      )

    State(
      out = first,
      next = verifying(rmh, jars)
    )
  }

  def verifying(
    rmh: RunMavenHierarchy,
    files: IndexedSeq[(String, File)]
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
            }) :+
            AkkaStreamCoding.pickle(
              Plug(
                rmh.request[Management.Plugger]
              )
            )
        })

    Future.successful(
      State(
        out,
        { data =>
          AkkaStreamCoding
            .unpickle[Management.Done.type ](data)
            .map({ done =>
              logger.info(s"done: ${done}")
              StateMachine.End
            })
        }
      )
    )
  }

}
