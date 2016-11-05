package toolbox8.jartree.akka

import akka.actor.{Actor, ActorRef}
import akka.actor.Actor.Receive
import akka.stream.actor.{ActorPublisher, ActorSubscriber, RequestStrategy}
import akka.stream.scaladsl.Sink
import akka.util.ByteString

/**
  * Created by maprohu on 05-11-2016.
  */
import StreamReceiverActor._

class StreamReceiverActor extends ActorPublisher[ByteString] {


  override def receive: Receive = ???

}

object StreamReceiverActor {
  val BufferSize = 16

  case class SetSource(
    source: ActorRef
  )
}
