package toolbox8.jartree.client

import java.io.File
import java.nio.ByteBuffer

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{FileIO, Flow, Keep, Sink, Source, Tcp}
import akka.util.ByteString
import maven.modules.builder.NamedModule
import monix.execution.Cancelable
import monix.reactive.Observable
import monix.reactive.subjects.PublishToOneSubject
import org.reactivestreams.{Processor, Publisher, Subscriber, Subscription}
import toolbox6.jartree.client.JarTreeClient
import toolbox6.jartree.packaging.JarTreePackaging
import toolbox6.jartree.packaging.JarTreePackaging.{RunHierarchy, RunMavenHierarchy}
import toolbox8.jartree.protocol.JarTreeStandaloneProtocol.{Framing, Management}
import toolbox8.jartree.protocol.JarTreeStandaloneProtocol.Management.{PlugRequest, PutHeader, VerifyRequest, VerifyResponse}
import toolbox8.jartree.protocol.{JarTreeStandaloneProtocol}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
//import monix.execution.Scheduler.Implicits.global

/**
  * Created by martonpapp on 16/10/16.
  */
object JarTreeStandaloneClient {

  def run(
    host: String,
    port: Int,
    runHierarchy: RunHierarchy,
    target: NamedModule
  ) = {

    implicit val actorSystem = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val peer =
      Tcp()
        .outgoingConnection(
          host,
          port
        )

    val flow =
      Flow
        .fromSinkAndSourceMat(
          Sink.asPublisher[ByteString](false),
          Source.asSubscriber[ByteString]
        )(Keep.both)

    val (pub, sub) =
      peer
        .join(
          Framing.Akka
        )
        .joinMat(
          flow
        )(Keep.right)
        .run()

    process(pub, sub, runHierarchy, target)
  }

  case class State(
    out: Observable[ByteString] = Observable.empty,
    fn: ByteString => State
  )

  def process(
    pub: Publisher[ByteString],
    sub: Subscriber[ByteString],
    runHierarchy: RunHierarchy,
    target: NamedModule
  )(implicit
    materializer: Materializer
  ) = {
    import monix.execution.Scheduler.Implicits.global



    val Header = ByteString(Management.Header.toByte)

    val init =
      start(runHierarchy, target)

    Observable
      .fromReactivePublisher(pub)
      .map({ bs =>
        require(bs(0) == Management.Header)
        bs.tail
      })
      .scan(
        init
      )({ (state, in) =>
        state.fn(in)
      })
      .startWith(Seq(init))
      .flatMap(_.out)
      .map(bs => Header ++ bs)
      .dump("x")
      .subscribe(
        monix.reactive.observers.Subscriber.fromReactiveSubscriber(
          sub,
          Cancelable.empty
        )
      )


  }

  import boopickle.Default._

  implicit class ByteBuffersOps(bbs: Iterable[ByteBuffer]) {
    def asByteString : ByteString = {
      bbs
        .map(ByteString.apply)
        .foldLeft(ByteString.empty)(_ ++ _)
    }

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

    val jars = JarTreeClient.resolverJarsFile(rmh)

    val first =
      Observable(
        Pickle
          .intoByteBuffers(
            VerifyRequest(
              ids = jars.map(_._1)
            )
          )
          .asByteString
      )

    State(
      out = first,
      fn = verifying(rmh, jars.map(_._2))
    )
  }

  def verifying(
    rmh: RunMavenHierarchy,
    files: IndexedSeq[File]
  )(
    bs: ByteString
  )(implicit
    materializer: Materializer
  ) : State = {
    val res = Unpickle[VerifyResponse].fromBytes(bs.asByteBuffer)

    val mfiles =
      res
        .missing
        .map(files)

    val out = Observable.concat(
      Observable(
        Pickle
          .intoByteBuffers(
            PutHeader(
              mfiles
                .map(_.length())
            )
          )
          .asByteString
      ),
      Observable
        .fromIterable(mfiles)
        .concatMap({ f =>
          Observable
            .fromReactivePublisher(
              FileIO
                .fromPath(
                  f.toPath,
                  JarTreeStandaloneProtocol.Framing.MaxSize - 100
                )
                .runWith(
                  Sink.asPublisher(false)
                )
            )
        }),
      Observable(
        Pickle
          .intoByteBuffers(
            PlugRequest(
              rmh.request[Management.Plugger],
              Array.emptyByteArray
            )
          )
          .asByteString
      )
    )

    end(out)
  }

  def end(out: Observable[ByteString]) : State = State(
    out = out,
    fn = _ => end(Observable.empty)
  )





}
