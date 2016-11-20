package toolbox8.akka.actor

import akka.actor.Actor
import akka.event.Logging

/**
  * Created by maprohu on 12-11-2016.
  */
class DumpLogActor extends Actor {
  val log = Logging(context.system, this)
  override def receive: Receive = {
    case msg =>
      log.info("dump: {}", msg)
  }
}
