package toolbox8.jartree.app

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import toolbox6.logging.LogTools
import toolbox8.jartree.standaloneapi.Protocol

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by maprohu on 05-11-2016.
  */
object JarTreeAkkaMain extends LazyLogging with LogTools {

  def main(args: Array[String]): Unit = {
    val (log, name) = JarTreeMain.parseName(args)

    val address = if (args.length >= 2) {
      args(1)
    } else {
      "localhost"
    }

    run(name, address)

  }

  def run(
    name: String,
    address: String
  ) = {


    implicit val actorSystem = ActorSystem(
      name,
      ConfigFactory.parseString(
        s"""
           |akka {
           |  loggers = ["akka.event.slf4j.Slf4jLogger"]
           |  logging-filter = "akka.event.slf4j.Slf4jLoggingFilter"
           |  loglevel = "DEBUG"
           |  jvm-exit-on-fatal-error = false
           |  actor {
           |    provider = remote
           |  }
           |  remote {
           |    enabled-transports = ["akka.remote.netty.tcp"]
           |    netty.tcp {
           |      hostname = "${address}"
           |      port = ${Protocol.AkkaDefaultPort}
           |    }
           |  }
           |}
        """.stripMargin
      ).withFallback(ConfigFactory.load())
    )


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
