package toolbox8.jartree.testing

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import toolbox8.jartree.akka.StreamReceiverActor

/**
  * Created by maprohu on 05-11-2016.
  */
object RunStreamActor {

  def main(args: Array[String]): Unit = {

    implicit val actorSystem = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val sink =
      Sink
        .foreach(
          println
        )

    val receiver =
      actorSystem.actorOf(
        Props(
          classOf[StreamReceiverActor],
          StreamReceiverActor.Config(
            sink
          )
        )
      )

    val source =
      Source(Stream.from(0))
        .map(i => ByteString(i.toString))
        .take(10)





  }

}
