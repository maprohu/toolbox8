package toolbox8.jartree.standalone

import java.nio.file.Path

import akka.actor.Actor
import akka.actor.Actor.Receive
import toolbox6.jartree.impl.JarTreeBootstrap.Initializer
import toolbox6.jartree.impl.{JarCache, JarTree, JarTreeBootstrap}
import scala.collection.immutable._

/**
  * Created by maprohu on 05-11-2016.
  */
import JarTreeActor._
class JarTreeActor(
  config: Config
) extends Actor {
  import toolbox8.jartree.standaloneapi.JarTreeAkkaApi._

  var runtime : JarTreeBootstrap.Runtime[Processor, Context] = null


  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    import config._

    import monix.execution.Scheduler.Implicits.global
    runtime = JarTreeBootstrap
      .init[Processor, Context](
        JarTreeBootstrap.Config(
          contextProvider = { (jt, ctx) =>
            ()
          },
          voidProcessor = (),
          name = name,
          dataPath = dataPath.toFile.getAbsolutePath,
          version = version,
          initializer = { () =>
            Initializer(
              Seq(),
              None
            )
          },
          closer = { _ => },
          logFile = logFile.map(_.toFile),
          storageDir = storageDir.map(_.toFile)
        )
      )

  }


  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    runtime.stop.cancel()

    super.postStop()
  }

  override def receive: Receive = {
    case Query =>
      sender ! runtime.processorSocket.query()
  }
}

object JarTreeActor {
  case class Config(
    name: String,
    dataPath: Path,
    version: Option[String],
    logFile: Option[Path],
    storageDir: Option[Path]
  )

  case object Query


}
