package toolbox8.jartree.akka

/**
  * Created by maprohu on 05-11-2016.
  */
import akka.actor.{Actor, ActorRef}
import akka.actor.Actor.Receive
import akka.pattern._

import scala.collection.immutable._
import scala.concurrent.Future
import BufferedReceiverActor._
import akka.Done
import akka.stream.scaladsl.SourceQueueWithComplete
import toolbox8.jartree.akka.BufferedSenderActor.{Complete, Data}
class BufferedReceiverActor(
  config: Config[Any]
) extends Actor {
  import config._
  import context.dispatcher

  var buffer = Seq.empty[(Any, ActorRef)]
  var offering = false
  var complete = false

  def doOffer() = {
    offering = true
    val (msg, ref) = buffer.head
    buffer = buffer.tail
    queue.offer(msg)
      .map({ _ =>
        ref ! Done

        Done
      })
      .pipeTo(self)
  }

  override def receive: Receive = {
    case d : Data =>
      buffer :+= (d.msg, sender())
      if (!offering) {
        doOffer ()
      }

    case Done =>
      offering = false
      if (buffer.nonEmpty) {
        doOffer()
      } else if (complete) {
        queue.complete()
        context stop self
      }

    case Complete =>
      complete = true
      if (!offering) {
        queue.complete()
        context stop self
      }

    case BufferedSenderActor.Error =>
      queue.fail(new Exception())
      context stop self


  }
}
object BufferedReceiverActor {
  case class Config[T](
    queue: SourceQueueWithComplete[T]
  )


}
