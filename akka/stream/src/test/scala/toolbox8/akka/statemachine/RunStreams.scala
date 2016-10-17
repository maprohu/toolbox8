package toolbox8.akka.statemachine

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Framing}
import akka.util.ByteString

/**
  * Created by martonpapp on 17/10/16.
  */
object RunStreams {

  val Mid = 0
  val Start = 1
  val End = 2

  def main(args: Array[String]): Unit = {
    implicit val actorSystem = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val peer : Flow[ByteString, ByteString, Any] = ???
    val MaxFrameSize = 1024 * 32

    val framing = Framing.simpleFramingProtocol(MaxFrameSize)

    peer
      .join(framing.reversed)
      .splitWhen(_(0) == Start)
      .to()



  }

}
