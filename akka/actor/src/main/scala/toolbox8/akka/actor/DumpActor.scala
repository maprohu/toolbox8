package toolbox8.akka.actor

import akka.actor.Actor
import akka.actor.Actor.Receive

/**
  * Created by maprohu on 12-11-2016.
  */
class DumpActor extends Actor {
  override def receive: Receive = {
    case msg =>
      println(msg)
  }
}
