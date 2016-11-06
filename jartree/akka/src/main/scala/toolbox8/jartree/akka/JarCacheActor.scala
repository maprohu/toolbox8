package toolbox8.jartree.akka

import java.nio.file.Path

import akka.actor.{Actor, ActorRef, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString
import toolbox8.jartree.akka.JarCacheUploaderActor.{JarRequest, JarsRequest}
import toolbox8.jartree.akka.StreamSenderActor.ChunksRequest

import scala.collection.immutable._

/**
  * Created by maprohu on 05-11-2016.
  */
import JarCacheActor._

class JarCacheActor(
  dir: Path
) extends Actor {
  implicit val materializer = ActorMaterializer()


  var state = State()

  override def receive: Receive = {
    case v : VerifyRequest =>
      val missing : Map[ActorRef, (JarKey, Long)] =
        v
          .keys
          .to[Set]
          .--(state.lookup.keySet)
          .zipWithIndex
          .map({
            case (key, idx) =>
              val id = state.nextId + idx
              val receiver =
                Source
                  .actorPublisher[ByteString](
                    Props[StreamReceiverActor]
                  )
                  .to(
                    FileIO
                      .toPath(
                        dir.resolve(s"${id}.jar")
                      )
                  )
                  .run

              context.watch(receiver)

              receiver -> (key, id)
          })
          .toMap

      state = state.copy(
        nextId = state.nextId + missing.size,
        running = state.running ++ missing
      )

      sender() ! JarsRequest(
        requests = missing
          .to[Seq]
          .map({
            case (ref, (key, id)) =>
              JarRequest(
                key = key,
                target = ref,
                chunks = ChunksRequest(
                  StreamReceiverActor.BufferSize
                )
              )
          })
      )

  }

}

object JarCacheActor {

  case class JarKey(
    groupId: String,
    artifactId: String,
    version: String,
    classifier: Option[String] = None,
    hash: Option[String] = None
  )

  case class State(
    nextId: Long = 0,
    lookup: Map[JarKey, Long] = Map.empty,
    running: Map[ActorRef, (JarKey, Long)] = Map.empty
  )

  case class VerifyRequest(
    keys: Iterable[JarKey]
  )


}
