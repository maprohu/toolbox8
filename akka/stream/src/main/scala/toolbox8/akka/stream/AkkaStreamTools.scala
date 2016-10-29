package toolbox8.akka.stream

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Materializer, Supervision}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

/**
  * Created by pappmar on 19/10/2016.
  */
object AkkaStreamTools extends LazyLogging {

  trait Components {
    implicit val actorSystem: ActorSystem
    implicit val materializer: Materializer
    implicit def dispatcher = actorSystem.dispatcher
  }

  def bootstrap(debug: Boolean = false) : Components = {
    implicit val _actorSystem = ActorSystem(
      "streamtools",
      ConfigFactory.parseString(
        s"""
          |akka {
          |  ${
                if (!debug) {
                  """
                    |  loggers = ["akka.event.slf4j.Slf4jLogger"]
                    |  loglevel = "INFO"
                    |  logging-filter = "akka.event.slf4j.Slf4jLoggingFilter"
                  """.stripMargin
                } else {
                  """
                    |  loglevel = "DEBUG"
                  """.stripMargin
                }
             }
          |  jvm-exit-on-fatal-error = false
          |}
        """.stripMargin
      ).withFallback(ConfigFactory.load()),
      AkkaStreamTools.getClass.getClassLoader
    )

    implicit val _materializer =
      ActorMaterializer(
        Some(
          ActorMaterializerSettings(_actorSystem)
            .withSupervisionStrategy({ ex:Throwable =>
              logger.error(ex.getMessage, ex)

              Supervision.Stop
            })
        )
      )

    new Components {
      override implicit val actorSystem: ActorSystem = _actorSystem
      override implicit val materializer: Materializer = _materializer
    }
  }

  lazy val Info = bootstrap(false)
  lazy val Debug = bootstrap(true)

}
