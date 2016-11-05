package toolbox8.jartree.standalone

import java.nio.file.Paths

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import toolbox6.logging.LogTools
import toolbox8.jartree.standalone.JarTreeActor.Config
import toolbox8.jartree.standaloneapi.{JarTreeAkkaApi, Protocol}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by maprohu on 05-11-2016.
  */
object JarTreeAkka extends LazyLogging with LogTools {

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

    val basePath = Paths.get(s"/opt/${name}")

    val jarTreeRef = actorSystem.actorOf(
      Props(
        classOf[JarTreeActor],
        Config(
          name = name,
          dataPath = basePath.resolve("data"),
          version = Option(getClass.getPackage.getImplementationVersion),
          logFile = Some(basePath.resolve(Paths.get("logs", s"${name}.log"))),
          storageDir = Some(basePath.resolve("storage"))
        )
      ),
      JarTreeAkkaApi.JarTreeActorName
    )

    logger.info(s"started: ${jarTreeRef}")

    sys.addShutdownHook {
      quietly {
        Await.result(
          actorSystem.terminate(),
          15.seconds
        )
      }
    }

    actorSystem
  }

}
