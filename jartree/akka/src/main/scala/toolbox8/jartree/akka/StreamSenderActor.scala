package toolbox8.jartree.akka

import akka.actor.{Actor, ActorRef}
import akka.actor.Actor.Receive
import akka.stream.ActorMaterializer
import akka.stream.actor.{ActorSubscriber, RequestStrategy}
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import toolbox8.jartree.akka.StreamSenderActor.{ChunksRequest, Config}

/**
  * Created by maprohu on 05-11-2016.
  */
class StreamSenderActor(config: Config) extends ActorSubscriber {
  import config._



  override def receive: Receive = {
    case r : ChunksRequest =>
  }

  override protected def requestStrategy: RequestStrategy = ???
}

object StreamSenderActor {
  case class Config(
    target: ActorRef
  )

  case class ChunksRequest(
    count: Int
  )

}
