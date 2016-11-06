package toolbox8.jartree.akka

import java.nio.file.Path

import akka.Done
import akka.actor.{Actor, ActorRef, Props, Terminated}
import akka.event.Logging
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{FileIO, Keep, Source}
import akka.util.ByteString
import toolbox8.jartree.akka.JarCacheUploaderActor.{JarRequest, JarsRequest}

import scala.collection.immutable._

/**
  * Created by maprohu on 05-11-2016.
  */
import JarCacheActor._

class JarCacheActor(
  config: Config
) extends Actor {
  val log = Logging(context.system, this)
  import context.dispatcher
  import config._
  implicit val materializer = ActorMaterializer()


  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()

    dir.toFile.mkdirs()
  }

  var state = State()

  def path(id: Long) = {
    dir.resolve(s"${id}.jar")
  }

  override def receive: Receive = {
    case v : VerifyRequest =>
      log.debug("received: {}", v)
      val from = sender()
      val missing =
        v
          .keys
          .to[Set]
          .--(state.lookup.keySet)
          .zipWithIndex
          .map({
            case (key, idx) =>
              val id = state.nextId + idx
              val (queue, io) =
                Source
                  .queue[ByteString](0, OverflowStrategy.backpressure)
                  .toMat(
                    FileIO
                      .toPath(
                        path(id)
                      )
                  )(Keep.both)
                  .run

              val receiver = context.actorOf(
                Props(
                  classOf[BufferedReceiverActor],
                  BufferedReceiverActor.Config(
                    queue
                  )
                )
              )


              io
                .onComplete({ r =>
                  log.debug("write result: {} for key: {}", r, key)
                })

              val finish = Finished(
                key = key,
                id = id,
                from = from
              )

              io
                .foreach({ _ =>
                  self ! finish
                })

              (receiver, finish)
          })

      state = state.copy(
        nextId = state.nextId + missing.size
      )

      sender() ! JarsRequest(
        requests = missing
          .to[Seq]
          .map({
            case (ref, r) =>
              JarRequest(
                key = r.key,
                target = ref
              )
          })
      )

    case running : Finished =>
//      val running = state.running(t.actor)
      log.debug("terminated: {}", running)

      running.from ! Done

      state = state.copy(
        lookup = state.lookup.updated(
          running.key,
          running.id
        )
      )

    case g : Get =>
      sender ! GetResponse(
        jars =
          g
            .keys
            .map({ k =>
              path(
                state
                  .lookup(k)
              )
            })
      )
  }

}

object JarCacheActor {

  case class Config(
    dir: Path
  )

  case class JarKey(
    groupId: String,
    artifactId: String,
    version: String,
    classifier: Option[String] = None,
    hash: Option[String] = None
  )

  case class Finished(
    key: JarKey,
    id: Long,
    from: ActorRef

  )

  case class State(
    nextId: Long = 0,
    lookup: Map[JarKey, Long] = Map.empty
  )

  case class VerifyRequest(
    keys: Iterable[JarKey]
  )

  case class Get(
    keys: Iterable[JarKey]
  )

  case class GetResponse(
    jars: Iterable[Path]
  )


}
