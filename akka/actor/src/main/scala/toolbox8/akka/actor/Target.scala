package toolbox8.akka.actor

import akka.actor.ActorRef

/**
  * Created by maprohu on 04-11-2016.
  */
class Target {
  var ref = Option.empty[ActorRef]

  def send(msg: Any) = ref.foreach(_ ! msg)
  def set(ref: ActorRef) = this.ref = Some(ref)

}

object Target {
  def apply(): Target = new Target()
}
