package toolbox8.jartree.akka

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import toolbox8.jartree.akka.JarCacheActor.{JarKey, VerifyRequest}
import toolbox8.jartree.akka.StreamSenderActor.ChunksRequest

import scala.collection.immutable._

/**
  * Created by maprohu on 05-11-2016.
  */
import JarCacheUploaderActor._

class JarCacheUploaderActor(
  config: Config
) extends Actor {
  import config._
  implicit val materializer = ActorMaterializer()


  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()

    cache ! VerifyRequest(
      keys
    )
  }

  override def receive: Receive = {
    case r : JarsRequest =>
      r
        .requests
        .foreach({ j =>
          val ref =
            resources(j.key)
              .runWith(
                Sink
                  .actorSubscriber(
                    Props(
                      classOf[StreamSenderActor],
                      StreamSenderActor.Config(
                        j.target
                      )
                    )
                  )
              )

          ref ! j.chunks
        })


  }

}

object JarCacheUploaderActor {
  case class Config(
    cache: ActorRef,
    keys: Iterable[JarKey],
    resources: JarKey => Source[ByteString, _]
  )


  case class JarRequest(
    key: JarKey,
    target: ActorRef,
    chunks: ChunksRequest
  )

  case class JarsRequest(
    requests: Iterable[JarRequest]
  )

}