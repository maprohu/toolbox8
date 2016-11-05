package toolbox8.akka.actor

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by maprohu on 05-11-2016.
  */
object ActorSystemTools {

  def actorSystem(
    name: String,
    address: String,
    port: Int
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
