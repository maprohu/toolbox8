package toolbox8.akka.actor

import java.io.File
import java.nio.file.Path

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
    port: Int = 5555,
    persistence: Option[Path] = None
  ) = {
    val base = persistence.getOrElse(
      new File("../toolbox8/target/persistence").toPath
    )
    ActorSystem(
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
           |  persistence {
           |    journal {
           |      plugin = "akka.persistence.journal.leveldb"
           |      leveldb {
           |        dir = "${base.resolve("journal").toFile.getAbsolutePath}"
           |      }
           |    }
           |    snapshot-store {
           |      plugin = "akka.persistence.snapshot-store.local"
           |      local {
           |        dir = "${base.resolve("snapshots").toFile.getAbsolutePath}"
           |      }
           |    }
           |  }
           |}
        """.stripMargin
      ).withFallback(ConfigFactory.load())
    )
  }


}
