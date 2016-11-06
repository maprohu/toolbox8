package toolbox8.jartree.akka

import akka.Done
import akka.actor.{Actor, ActorRef}
import akka.util.ByteString

import scala.collection.immutable._
import scala.concurrent.Future
import akka.pattern._
import akka.stream.scaladsl.SinkQueueWithCancel
/**
  * Created by maprohu on 05-11-2016.
  */
import BufferedSenderActor._

class BufferedSenderActor(
  config: Config[Any]
) extends Actor {
  import config._
  import context.dispatcher

  def doPoll() = {
    queue
      .pull()
      .map({ m =>
        m
          .map(Data.apply)
          .getOrElse(Complete)
      })
      .recover({ case _ => Error })
      .pipeTo(self)
  }

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()

    doPoll()
  }

  var pending = 0
  var complete = false

  override def receive: Receive = {
    case d : Data =>
      target ! d
      pending += 1
      if (pending < bufferSize) {
        doPoll()
      }

    case Done =>
      if (pending == bufferSize) {
        doPoll()
      }
      pending -= 1
      if (complete && pending == 0) {
        target ! Complete
        context stop self
      }

    case Complete =>
      complete = true
      if (pending == 0) {
        target ! Complete
        context stop self
      }

    case Error =>
      target ! Error
      context stop self

  }
}

object BufferedSenderActor {
  case class Config[T](
    target: ActorRef,
    queue: SinkQueueWithCancel[T],
    bufferSize: Int = 32
  )

  case class Data(
    msg: Any
  )

  case object Complete
  case object Error
}
