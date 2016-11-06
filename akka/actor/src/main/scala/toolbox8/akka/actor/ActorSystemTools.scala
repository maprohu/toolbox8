package toolbox8.akka.actor

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by maprohu on 05-11-2016.
  */
object ActorSystemTools {

  object Implicit {
    implicit lazy val actorSystem = ActorSystemTools.actorSystem()
  }

  def actorSystem(
    name: String = "toolbox8",
    address: String = "localhost",
    port: Int = 5555
  ) = ActorSystem(
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
         |      port = ${port}
         |    }
         |  }
         |}
        """.stripMargin
    ).withFallback(ConfigFactory.load())
  )


}
