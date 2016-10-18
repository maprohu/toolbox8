package toolbox8.jartree.client

import java.io.File
import java.nio.ByteBuffer

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{FileIO, Flow, Keep, Sink, Source, Tcp}
import akka.util.ByteString
import maven.modules.builder.NamedModule
import toolbox6.jartree.packaging.JarTreePackaging
import toolbox6.jartree.packaging.JarTreePackaging.{RunHierarchy, RunMavenHierarchy}
import toolbox8.akka.statemachine.AkkaStreamCoding.StateMachine
import toolbox8.akka.statemachine.{AkkaStreamCoding, DeepStream}
import toolbox8.jartree.protocol.JarTreeStandaloneProtocol
import toolbox8.jartree.protocol.JarTreeStandaloneProtocol.Management
import toolbox8.jartree.protocol.JarTreeStandaloneProtocol.Management.{PlugRequest, VerifyRequest, VerifyResponse}

import scala.collection.immutable
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
//import monix.execution.Scheduler.Implicits.global

/**
  * Created by martonpapp on 16/10/16.
  */
object JarTreeStandaloneClient {

  type ByteFlow = Flow[ByteString, ByteString, NotUsed]

  def run(
    host: String,
    port: Int,
    runHierarchy: RunHierarchy,
    target: NamedModule
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

//    val flow =
//      Flow
//        .fromSinkAndSourceMat(
//          Sink.asPublisher[ByteString](false),
//          Source.asSubscriber[ByteString]
//        )(Keep.both)


    val management : ByteFlow = managementFlow(
      runHierarchy,
      target
    )
    val data : ByteFlow =
      Flow.fromSinkAndSource(
        Sink.ignore,
        Source.maybe
      )

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

    val jars = JarTreePackaging.resolverJarsFile(rmh)

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
      next = verifying(rmh, jars.map(_._2))
    )
  }

  def verifying(
    rmh: RunMavenHierarchy,
    files: IndexedSeq[File]
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
            res
              .missing
              .map(files)
        })

    val out =
      Source
        .fromFuture(
          mfilesF
        )
        .mapConcat({ mfiles =>
//          immutable.Iterable(
//            Source.single(
//              Pickle
//                .intoByteBuffers(
//                  PutHeader(
//                    mfiles.map(_.length())
//                  )
//                )
//                .asByteString
//            )
//          ) ++
          mfiles
            .map({ elem =>
              FileIO.fromPath(elem.toPath)
            }) :+
            AkkaStreamCoding.pickle(
              PlugRequest(
                rmh.request[Management.Plugger],
                Array.emptyByteArray
              )
            )
        })

    Future.successful(end(out))
  }

  def end(out: Source[AkkaStreamCoding.Data, Any]) : State = {
    AkkaStreamCoding.StateMachine.State(
      out,
      _ => Future.successful(StateMachine.End)
    )
  }





}
