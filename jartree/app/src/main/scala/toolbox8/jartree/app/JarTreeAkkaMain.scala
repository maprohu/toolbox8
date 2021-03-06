package toolbox8.jartree.app

import com.typesafe.scalalogging.LazyLogging
import toolbox6.logging.LogTools
import toolbox8.jartree.akka.JarTreeAkka._
import toolbox8.jartree.akka.{JarTreeAkka, JarTreeAkkaApi, PluggableServiceActor}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by maprohu on 05-11-2016.
  */
object JarTreeAkkaMain extends LazyLogging with LogTools {

  val DefaultPort = 5500

  def main(args: Array[String]): Unit = {
    val (log, name) = JarTreeMain.initLogging(args)

    val port = if (args.length >= 2) {
      args(1).toInt
    } else {
      DefaultPort
    }

    val out = JarTreeAkka
      .run(
        name,
        "localhost",
        port
      )

    import out._

    sys.addShutdownHook {
      quietly {
        Await.result(
          actorSystem.terminate(),
          15.seconds
        )
      }
    }
  }



}
