package toolbox8.jartree.client

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source, Tcp}
import akka.util.ByteString
import maven.modules.builder.NamedModule
import monix.execution.Cancelable
import monix.reactive.Observable
import monix.reactive.subjects.PublishToOneSubject
import org.reactivestreams.{Processor, Publisher, Subscriber, Subscription}
import toolbox6.jartree.client.JarTreeClient
import toolbox6.jartree.packaging.JarTreePackaging
import toolbox6.jartree.packaging.JarTreePackaging.RunHierarchy
import toolbox8.jartree.protocol.JarTreeStandaloneProtocol.Management.VerifyRequest
import toolbox8.jartree.protocol.{ByteArrayImpl, JarTreeStandaloneProtocol}

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

    val flow =
      Flow
        .fromSinkAndSourceMat(
          Sink.asPublisher[ByteString](false),
          Source.asSubscriber[ByteString]
        )(Keep.both)

    val (pub, sub) =
      Tcp()
        .outgoingConnection(
          host,
          port
        )
        .joinMat(
          flow
        )(Keep.right)
        .run()

    process(pub, sub, runHierarchy, target)
  }

  def process(
    pub: Publisher[ByteString],
    sub: Subscriber[ByteString],
    runHierarchy: RunHierarchy,
    target: NamedModule
  ) = {
    import monix.execution.Scheduler.Implicits.global

    val rmh =
      runHierarchy
        .forTarget(
          JarTreePackaging.target(
            target
          )
        )

    val jars = JarTreeClient.resolverJars(rmh)

    import boopickle.Default._

    val input =
      Observable
        .fromReactivePublisher(pub)
        .map(bs => ByteArrayImpl(bs.toArray))
        .transform(JarTreeStandaloneProtocol.Framing.Decoder)
        .transform(JarTreeStandaloneProtocol.Multiplex.DropHeader)
        .map(_ => ByteArrayImpl.Empty)

    val first =
      Observable(
        ByteArrayImpl(
          Pickle
            .intoBytes(
              VerifyRequest(
                ids = jars.map(_._1)
              )
            )
        )
      )

    val Header = ByteArrayImpl(Array[Byte](0.toByte))

    Observable
      .concat(
        first,
        input
      )
      .map(bs => Iterable(Header, bs))
      .transform(JarTreeStandaloneProtocol.Framing.Encoder)
      .map(ba => ByteString.fromArray(ba.bytes(), ba.offset(), ba.count()))
      .dump("x")
      .subscribe(
        monix.reactive.observers.Subscriber.fromReactiveSubscriber(
          sub,
          Cancelable.empty
        )
      )


  }



}
