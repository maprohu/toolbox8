package toolbox8.jartree.akka

import akka.actor.ActorRef
import akka.persistence.{PersistentActor, RecoveryCompleted}
import toolbox8.jartree.akka.JarCacheActor.JarKey

import scala.collection.immutable._
import scala.concurrent.Future

/**
  * Created by maprohu on 06-11-2016.
  */
import PluggableServiceActor._
class PluggableServiceActor(
  config: Config
) extends PersistentActor {
  import config._

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

  def doPlugging() = {
    plugging = true

    for {
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
    }


  }

  override def receiveRecover: Receive = {
    case RecoveryCompleted =>
      doPlugging()

    case e : Evt =>
      updateState(e)

  }

  override def receiveCommand: Receive = {

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


  case class Config(
    uniqueId: String,
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


