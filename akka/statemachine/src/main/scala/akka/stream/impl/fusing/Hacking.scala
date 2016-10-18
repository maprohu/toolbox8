package akka.stream.impl.fusing

import java.util.concurrent.CompletionStage

import akka.stream.ActorAttributes.SupervisionStrategy
import akka.stream._
import akka.stream.impl.ReactiveStreamsCompliance
import akka.stream.scaladsl.Flow
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}

import scala.annotation.unchecked.uncheckedVariance
import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}
import scala.compat.java8.FutureConverters._


object Hacking {

  implicit class FlowOpsExt[In, Out, Mat](flow: Flow[In, Out, Mat]) {
    type Repr[+O] = Flow[In @uncheckedVariance, O, Mat @uncheckedVariance]

    /**
      * Similar to `scan` but with a asynchronous function,
      * emits its current value which starts at `zero` and then
      * applies the current and next value to the given function `f`,
      * emitting a `Future` that resolves to the next current value.
      *
      * If the function `f` throws an exception and the supervision decision is
      * [[akka.stream.Supervision.Restart]] current value starts at `zero` again
      * the stream will continue.
      *
      * If the function `f` throws an exception and the supervision decision is
      * [[akka.stream.Supervision.Resume]] current value starts at the previous
      * current value, or zero when it doesn't have one, and the stream will continue.
      *
      * '''Emits when''' the future returned by f` completes
      *
      * '''Backpressures when''' downstream backpressures
      *
      * '''Completes when''' upstream completes and the last future returned by `f completes
      *
      * '''Cancels when''' downstream cancels
      *
      * See also [[akka.stream.scaladsl.FlowOps.scan]]
      */
    def scanAsync[T](zero: T)(f: (T, Out) ⇒ Future[T]): Repr[T] = flow.via(ScanAsync(zero, f))


  }

}

/**
  * INTERNAL API
  */
final case class ScanAsync[In, Out](zero: Out, f: (Out, In) ⇒ Future[Out]) extends GraphStage[FlowShape[In, Out]] {

  import akka.dispatch.ExecutionContexts

  val in = Inlet[In]("ScanAsync.in")
  val out = Outlet[Out]("ScanAsync.out")
  override val shape: FlowShape[In, Out] = FlowShape[In, Out](in, out)

  override val initialAttributes: Attributes = Attributes.name("scanAsync")

  override val toString: String = "ScanAsync"

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) with InHandler with OutHandler {
      self ⇒

      private var current: Out = zero
      private var eventualCurrent: Future[Out] = Future.successful(current)

      private def ec = ExecutionContexts.sameThreadExecutionContext

      private lazy val decider = inheritedAttributes.get[SupervisionStrategy].map(_.decider).getOrElse(Supervision.stoppingDecider)

      private val ZeroHandler: OutHandler with InHandler = new OutHandler with InHandler {
        override def onPush(): Unit = ()

        override def onPull(): Unit = {
          push(out, current)
          setHandlers(in, out, self)
        }

        override def onUpstreamFinish(): Unit = setHandler(out, new OutHandler {
          override def onPull(): Unit = {
            push(out, current)
            completeStage()
          }
        })
      }

      private def onRestart(t: Throwable): Unit = {
        current = zero
      }

      private def safePull(): Unit = {
        if (!hasBeenPulled(in)) {
          tryPull(in)
        }
      }

      private def pushAndPullOrFinish(update: Out): Unit = {
        push(out, update)
        if (isClosed(in)) {
          completeStage()
        } else if (isAvailable(out)) {
          safePull()
        }
      }

      private def doSupervision(t: Throwable): Unit = {
        decider(t) match {
          case Supervision.Stop   ⇒ failStage(t)
          case Supervision.Resume ⇒ safePull()
          case Supervision.Restart ⇒
            onRestart(t)
            safePull()
        }
      }

      private val futureCB = getAsyncCallback[Try[Out]] {
        case Success(next) if next != null ⇒
          current = next
          pushAndPullOrFinish(next)
        case Success(null) ⇒ doSupervision(ReactiveStreamsCompliance.elementMustNotBeNullException)
        case Failure(t)    ⇒ doSupervision(t)
      }.invoke _

      setHandlers(in, out, ZeroHandler)

      def onPull(): Unit = safePull()

      def onPush(): Unit = {
        try {
          eventualCurrent = f(current, grab(in))

          eventualCurrent.value match {
            case Some(result) ⇒ futureCB(result)
            case _            ⇒ eventualCurrent.onComplete(futureCB)(ec)
          }
        } catch {
          case NonFatal(ex) ⇒
            decider(ex) match {
              case Supervision.Stop    ⇒ failStage(ex)
              case Supervision.Restart ⇒ onRestart(ex)
              case Supervision.Resume  ⇒ ()
            }
            tryPull(in)
        }
      }

      override def onUpstreamFinish(): Unit = {}

      override val toString: String = s"ScanAsync.Logic(completed=${eventualCurrent.isCompleted})"
    }
}