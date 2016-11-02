package toolbox8.akka.stream

import java.io.OutputStream

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.scaladsl.{Flow, Keep, Source, StreamConverters}
import akka.stream._
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import monix.execution.Cancelable
import monix.execution.cancelables.AssignableCancelable

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



  object Implicits {
    implicit class FlowExt[In, Out, Mat](flow: Flow[In, Out, Mat]) {

    }
  }

}

object Flows {

  def stopper[T] : Flow[T, T, Cancelable] = {
    Flow[T]
      .map(Some.apply)
      .mergeMat(
        Source
          .maybe[Option[Nothing]]
          .mapMaterializedValue({ p =>
            Cancelable({ () =>
              p.success(None)
            })
          }),
        true
      )(Keep.right)
      .takeWhile(_.isDefined)
      .map(_.get)
  }
  val Dump =
    Flow
      .fromSinkAndSource(
        Flow[ByteString]
          .log("dump").withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
          .to(
            Sinks.Dump
          ),
        Source.maybe
      )

}

object Sinks {

  val SystemOutUnclosed = new OutputStream {
    override def write(b: Int): Unit = System.out.write(b)


    override def flush(): Unit = {
      super.flush()
      System.out.flush()
    }

    override def close(): Unit = {
      super.close()
      flush()
    }
  }

  val Dump =
    StreamConverters
      .fromOutputStream(() => SystemOutUnclosed, true)

}

