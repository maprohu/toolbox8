package toolbox8.akka.actor

import akka.actor.{Actor, ActorRef, ActorRefFactory, ActorRefProvider, Props, Terminated}
import akka.actor.Actor.Receive

import scala.concurrent.{Future, Promise}

/**
  * Created by maprohu on 06-11-2016.
  */
object ActorTools {

  def watchFuture(
    ref: ActorRef
  )(implicit
    actorRefProvider: ActorRefFactory
  ) : Future[Terminated] = {
    val promise = Promise[Terminated]()

    actorRefProvider.actorOf(
      Props(
        classOf[WatchActor],
        WatchActor.Config(
          ref = ref,
          promise = promise
        )
      )
    )

    promise.future
  }

}

import WatchActor._
class WatchActor(
  config: Config
) extends Actor {
  import config._


  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    context.watch(ref)
  }

  override def receive: Receive = {
    case t : Terminated =>
      promise.success(t)
  }
}

object WatchActor {
  case class Config(
    ref: ActorRef,
    promise: Promise[Terminated]
  )
}

case class SetOut(
  ref: ActorRef
)
