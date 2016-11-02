package toolbox8.akka.actor

import akka.Done
import akka.actor.{Actor, ActorRef}
import akka.actor.Actor.Receive

import scala.collection.immutable._
import scala.concurrent.Future
import akka.pattern._
import toolbox8.akka.actor.ActorImplicits._

/**
  * Created by maprohu on 02-11-2016.
  */
class PubSubActor extends Actor {
  import PubSubActor._

  var state = State()

  override def receive: Receive = {
    case m : Subscribe =>
      state = state.copy(
        refs = state.refs + m.ref
      )
      sender() ! Done
    case m : Unsubscribe =>
      state = state.copy(
        refs = state.refs - m.ref
      )
      sender() ! Done
    case m : Publish =>
      val replyTo = sender()
      Future
        .sequence(
          state
            .refs
            .toSeq
            .map({ ref =>
              ref ? m.msg
            })
        )
        .foreach({ _ =>
          replyTo ! Done
        })
  }
}

object PubSubActor {

  case class Subscribe(ref: ActorRef)
  case class Unsubscribe(ref: ActorRef)
  case class Publish(msg: Any)

  private [actor] case class State(
    refs: Set[ActorRef] = Set.empty
  )

}
