package toolbox8.akka.stream

import java.io.OutputStream

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.scaladsl.{Flow, Keep, Sink, Source, StreamConverters}
import akka.stream._
import akka.stream.impl.fusing.{GraphStages, ScanAsync}
import akka.stream.stage.{GraphStageLogic, GraphStageWithMaterializedValue, OutHandler}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import monix.execution.Cancelable
import monix.execution.cancelables.{AssignableCancelable, BooleanCancelable}
import toolbox6.common.StringTools

import scala.concurrent.Future

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
          |  loggers = ["akka.event.slf4j.Slf4jLogger"]
          |  logging-filter = "akka.event.slf4j.Slf4jLoggingFilter"
          |  loglevel = "${if (debug) "DEBUG" else "INFO"}"
          |  jvm-exit-on-fatal-error = false
          |}
        """.stripMargin
      ).withFallback(ConfigFactory.load()),
      AkkaStreamTools.getClass.getClassLoader
    )

    val decider : Supervision.Decider = {
      case ex:Throwable =>
        logger.error(ex.getMessage, ex)
        Supervision.Stop
    }


    implicit val _materializer =
      ActorMaterializer(
        Some(
          ActorMaterializerSettings
            .apply(
              _actorSystem:ActorSystem
            )
            .withSupervisionStrategy(decider)
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
    implicit class SourceExt[Out, Mat](flow: Source[Out, Mat]) {
    }
    implicit class FlowExt[In, Out, Mat](flow: Flow[In, Out, Mat]) {
    }
  }

}

object Flows {

  def stopper[T] : Flow[T, T, BooleanCancelable] = {
    Flow[T]
      .map(Some.apply)
      .mergeMat(
        Source
          .maybe[Option[Nothing]]
          .mapMaterializedValue({ p =>
            BooleanCancelable({ () =>
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
          .log("dump")
          .to(
            Sinks.Dump
          ),
        Source.maybe
      )

  def stringResult(str: => String) = {
    Flow
      .fromSinkAndSource(
        Sink.ignore,
        Source
          .single(
            ByteString(
              StringTools.quietly(
                str
              )
            )
          )
      )
  }

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

object Sources {


  def singleMaterializedValue[T](fn: () => T) = {
    Source.fromGraph(new SingleMaterializedValueStage[T](fn))
  }

  class SingleMaterializedValueStage[T](fn: () => T) extends GraphStageWithMaterializedValue[SourceShape[T], T] {

    val out = Outlet[T]("single.out")
    val shape = SourceShape(out)

    @scala.throws[Exception](classOf[Exception])
    override def createLogicAndMaterializedValue(inheritedAttributes: Attributes): (GraphStageLogic, T) = {
      val elem = fn()

      val logic = new GraphStageLogic(shape) with OutHandler {
        def onPull(): Unit = {
          push(out, elem)
          completeStage()
        }
        setHandler(out, this)
      }

      (logic, elem)
    }

  }

}
