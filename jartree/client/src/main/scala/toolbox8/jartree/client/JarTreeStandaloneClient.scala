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
import toolbox8.akka.stream.AkkaStreamTools
import toolbox8.akka.stream.AkkaStreamTools.Components
import toolbox8.jartree.protocol.JarTreeStandaloneProtocol.Management
import toolbox8.jartree.protocol.JarTreeStandaloneProtocol.Management._
import toolbox8.jartree.standaloneapi.Protocol

import scala.concurrent.{Await, Future}

/**
  * Created by martonpapp on 16/10/16.
  */
object JarTreeStandaloneClient extends LazyLogging {

  type ByteFlow = Flow[ByteString, ByteString, _]

  val EmptyFlow : ByteFlow =
    Flow.fromSinkAndSource(
      Sink.ignore,
      Source.maybe
    )

  case class Flows(
    management: ByteFlow,
    data: ByteFlow
  )

  def run(
    host: String,
    port: Int,
    flows: Components => Flows
  ) = {

    val cmp = AkkaStreamTools.Info
    import cmp._

    val peer =
      Tcp()
        .outgoingConnection(
          host,
          port
        )

    val f = flows(cmp)
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
    port: Int = Protocol.DefaultPort,
    runHierarchy: RunHierarchy,
    target: NamedModule
  ) = {
    runManagement(
      host,
      port,
      { implicit mat =>
        import mat._
        upload(runHierarchy, target)
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
            upload(runHierarchy, target)
          )
      )

  }

  import boopickle.Default._
  import AkkaStreamCoding.Implicits._
  import AkkaStreamCoding.StateMachine.State

  def runCat(
    host: String,
    port: Int = Protocol.DefaultPort,
    sink: Components => Sink[ByteString, _]
  ) = {
    runData(
      host,
      port,
      mat => Flow.fromSinkAndSource(
        sink(mat),
        Source.maybe
      )
    )

  }
  def runData(
    host: String,
    port: Int = Protocol.DefaultPort,
    flow: Components => ByteFlow
  ) = {
    run(
      host,
      port,
      mat => Flows(
        management = EmptyFlow,
        data = flow(mat)
      )
    )
  }


  def runQuery(
    host: String,
    port: Int = Protocol.DefaultPort
  ) = {
    runManagement(
      host,
      port,
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
    state: Components => State
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

  def upload(
    runHierarchy: RunHierarchy,
    target: NamedModule,
    andThen: State
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
      next = verifying(rmh, jars, andThen)
    )
  }

  def verifying(
    rmh: RunMavenHierarchy,
    files: IndexedSeq[(String, File)],
    andThen: State
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
