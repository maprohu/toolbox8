package toolbox8.jartree.app

import com.typesafe.scalalogging.LazyLogging
import toolbox6.logging.LogTools
import toolbox8.jartree.akka.JarTreeAkka._
import toolbox8.jartree.akka.{JarTreeAkka, JarTreeAkkaApi}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by maprohu on 05-11-2016.
  */
object JarTreeAkkaMain extends LazyLogging with LogTools {

  def main(args: Array[String]): Unit = {
    val (log, name) = JarTreeMain.initLogging(args)

    val address = if (args.length >= 2) {
      args(1)
    } else {
      "localhost"
    }

    val out = JarTreeAkka
      .run(
        name,
        address,
        JarTreeAkkaApi.DefaultPort
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
