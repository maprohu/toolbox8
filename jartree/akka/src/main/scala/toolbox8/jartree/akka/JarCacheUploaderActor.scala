package toolbox8.jartree.akka

import akka.Done
import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, Props, Terminated}
import akka.event.Logging
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import toolbox8.jartree.akka.JarCacheActor.{JarKey, VerifyRequest}

import scala.collection.immutable._

/**
  * Created by maprohu on 05-11-2016.
  */
import JarCacheUploaderActor._

class JarCacheUploaderActor(
  config: Config
) extends Actor {
  val log = Logging(context.system, this)
  import config._
  implicit val materializer = ActorMaterializer()


  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()

    val request = VerifyRequest(
      keys
    )
    log.debug(
      "sending request: {}",
      request
    )

    cache ! request
  }

  var slots = Parrallelism
  var queue = Seq.empty[JarRequest]

  def doProcess() = {
    val (start, keep) = queue.splitAt(slots)

    start
      .foreach({ j =>
        val queue =
          resources(j.key)
            .runWith(
              Sink.queue()
            )

        context.actorOf(
          Props(
            classOf[BufferedSenderActor],
            BufferedSenderActor.Config(
              target = j.target,
              queue = queue
            )
          )
        )
      })

    queue = keep
    slots -= start.size

    if (queue.isEmpty && slots == Parrallelism) {
      log.debug("stopping")
      context.stop(self)
    }

  }

  override def receive: Receive = {
    case r : JarsRequest =>
      queue = r.requests.to[Seq]
      doProcess()


    case Done =>
      slots += 1
      log.debug("done, slots: {}", slots)
      doProcess()
  }

}

object JarCacheUploaderActor {
  val Parrallelism = 3

  case class Config(
    cache: ActorRef,
    keys: Iterable[JarKey],
    resources: JarKey => Source[ByteString, _]
  )


  case class JarRequest(
    key: JarKey,
    target: ActorRef
  )

  case class JarsRequest(
    requests: Iterable[JarRequest]
  )

}