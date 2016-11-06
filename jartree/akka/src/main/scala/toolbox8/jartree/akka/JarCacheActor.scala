package toolbox8.jartree.akka

import java.nio.file.Path

import akka.Done
import akka.actor.{Actor, ActorRef, Props, Terminated}
import akka.event.Logging
import akka.persistence.PersistentActor
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
) extends PersistentActor {
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

  override def receiveCommand: Receive = {
    case v : VerifyRequest =>
      log.debug("received: {}", v)

      val startId = state.nextId

      val missing =
        v
          .keys
          .to[Set]
          .--(state.lookup.keySet)

      persist(
        NextId(startId + missing.size)
      ) { e =>
        updateState(e)

        val from = sender()

        val requests =
          missing
            .zipWithIndex
            .map({
              case (key, idx) =>
                val id = startId + idx
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

        sender() ! JarsRequest(
          requests = requests
            .to[Seq]
            .map({
              case (ref, r) =>
                JarRequest(
                  key = r.key,
                  target = ref
                )
            })
        )


      }


    case running : Finished =>
      log.debug("terminated: {}", running)

      persist(
        PutEvent(
          key = running.key,
          id = running.id
        )
      ) { e =>
        updateState(e)

        running.from ! Done
      }


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

  def updateState(evt: Evt) = {
    evt match {
      case p : PutEvent =>
        state = state.copy(
          lookup = state.lookup.updated(
            p.key,
            p.id
          )
        )
      case i : NextId =>
        state = state.copy(
          nextId = i.id
        )
    }
  }



  override def receiveRecover: Receive = {
    case e : Evt =>
      updateState(e)
  }


  override def persistenceId: String = uniqueId
}

object JarCacheActor {
  sealed trait Evt
  case class PutEvent(
    id: Long,
    key: JarKey
  ) extends Evt
  case class NextId(
    id: Long
  ) extends Evt

  case class Config(
    uniqueId: String = "jar-cache",
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
