package toolbox8.jartree.akka

import akka.Done
import akka.actor.ActorRef
import akka.event.Logging
import akka.persistence.{PersistentActor, RecoveryCompleted}
import toolbox8.jartree.akka.JarCacheActor.JarKey

import scala.collection.immutable._
import scala.concurrent.Future
import akka.pattern._

/**
  * Created by maprohu on 06-11-2016.
  */
import PluggableServiceActor._
class PluggableServiceActor(
  config: Config
) extends PersistentActor {
  val log = Logging(context.system, this)
  import config._
  import context.dispatcher

  var state = State()
  var plugged : Plugged = VoidPlugged
  var plugging = false

  def updateState(e: Evt) = {
    e match {
      case p : PlugRequest =>
        state = state.copy(
          request = Some(p)
        )
    }
  }

  def doPlugging(
    replyTo: Option[ActorRef]
  ) = {
    log.info(s"plugging: ${state.request}")
    plugging = true

    val fut = for {
      pluggable <- {
        state
          .request
          .map({ r =>
            for {
              cl <- r
                .classLoader
                .map({ jars =>
                  ParentLastUrlClassloader(
                    jars = jars,
                    parent = parent,
                    jarCacheActor = cache
                  )
                })
                .getOrElse(
                  Future.successful(parent)
                )
            } yield {
              cl
                .loadClass(r.className)
                .asInstanceOf[Class[Pluggable]]
                .newInstance()
            }
          })
          .getOrElse(
            Future.successful(VoidPluggable)
          )
      }
      unplugged <- plugged.preUnplug()
      replugged <- pluggable.plug(
        PlugContext(
          previous = unplugged
        )
      )
    } yield {
      PluggingComplete(
        replugged,
        replyTo
      )
    }

    fut
      .pipeTo(self)

  }

  override def receiveRecover: Receive = {
    case RecoveryCompleted =>
      doPlugging(None)

    case e : Evt =>
      updateState(e)

  }

  override def receiveCommand: Receive = {
    case p : PluggingComplete =>
      log.info("plugging complete: {}", p)
      plugging = false
      plugged
        .postUnplug()
        .foreach({ _ =>
          log.info("postUnplug complete")
        })

      plugged = p.plugged
      unstashAll()
      p.replyTo.foreach(_ ! Done)

    case GetPlugged =>
      if (plugging) {
        stash()
      } else {
        sender() ! plugged
      }

    case p : PlugRequest =>
      if (plugging) {
        stash()
      } else {
        persist(p)({ p =>
          updateState(p)
          doPlugging(Some(sender()))
        })
      }

  }

  override def persistenceId: String = uniqueId
}

object PluggableServiceActor {
  sealed trait Evt
  sealed trait Cmd

  case class State(
    request: Option[PlugRequest] = None
  )

  case class PlugRequest(
    classLoader: Option[Seq[JarKey]] = None,
    className: String = classOf[VoidPluggable].getName
  ) extends Evt with Cmd

  case class PluggingComplete(
    plugged: Plugged,
    replyTo: Option[ActorRef]
  ) extends Cmd

  case object GetPlugged extends Cmd


  case class Config(
    uniqueId: String = "pluggable-service",
    cache: ActorRef,
    parent: ClassLoader
  )

  type Previous = Any
  case class PlugContext(
    previous: Previous
  )

  trait Plugged {
    def preUnplug() : Future[Previous] = Future.successful()
    def postUnplug() : Future[Any] = Future.successful()
  }
  object VoidPlugged extends Plugged

  trait Pluggable {
    def plug(
      context: PlugContext
    ) : Future[Plugged]
  }

  object VoidPluggable extends VoidPluggable
  class VoidPluggable extends Pluggable {
    override def plug(context: PlugContext): Future[Plugged] =
      Future.successful(VoidPlugged)
  }

}


