package toolbox8.jartree.testing

import akka.actor.{ActorSystem, Props}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import toolbox8.jartree.akka.{BufferedReceiverActor, BufferedSenderActor, StreamReceiverActor}

/**
  * Created by maprohu on 05-11-2016.
  */
object RunStreamActor {

  def main(args: Array[String]): Unit = {

    implicit val actorSystem = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val source =
      Source(Stream.from(0))
        .map(i => ByteString(i.toString))
        .take(10)
        .runWith(
          Sink.queue()
        )

    val sink =
      Sink
        .foreach[ByteString](
          println
        )
        .runWith(
          Source.queue[ByteString](0, OverflowStrategy.backpressure)
        )

    val receiver =
      actorSystem
        .actorOf(
          Props(
            classOf[BufferedReceiverActor],
            BufferedReceiverActor.Config(
              queue = sink
            )
          )
        )

    val sender =
      actorSystem
        .actorOf(
          Props(
            classOf[BufferedSenderActor],
            BufferedSenderActor.Config(
              target = receiver,
              queue = source
            )
          )
        )







  }

}
