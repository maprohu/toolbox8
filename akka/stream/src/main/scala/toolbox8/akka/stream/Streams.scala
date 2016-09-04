package toolbox8.akka.stream

import akka.stream.scaladsl.Flow
import akka.util.ByteString

import scala.collection.immutable._
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by martonpapp on 31/08/16.
  */
object Streams {

  class StateHolder[T](initial: T) {
    @volatile var state : T = initial
  }

  def statefulMapAsyncConcat[State, In, Out](
    initial: State,
    fn: (State, In) => Future[(State, Out)]
  )(implicit
    executionContext: ExecutionContext
  ) = {
    Flow[In]
      .statefulMapConcat({ () =>
        val holder = new StateHolder(initial)

        { in => Iterable((holder, in)) }
      })
      .mapAsync(1)({
        case (holder, in) =>
          fn(holder.state, in).map({
            case (state, out) =>
              holder.state = state
              out
          })
      })
  }

  def stateMachineMapAsyncConcat[In, Out](
    initial: State[In, Out]
  )(implicit
    executionContext: ExecutionContext
  ) = {
    statefulMapAsyncConcat[State[In, Out], In, Out](
      initial,
      (state, in) => state(in)
    )
  }

  trait State[In, Out] {

    def apply(in: In) : Future[(State[In, Out], Out)]

  }

  def groupBytes(groupSize: Int) =
    Flow[ByteString]
      .statefulMapConcat({ () =>
        var buffer = ByteString()

        { bs =>
          val b = buffer ++ bs

          def helper(acc: List[ByteString], from: Int, until: Int) : (Seq[ByteString], ByteString) = {
            if (b.length < until) (acc.reverse, b.slice(from, b.length))
            else helper(b.slice(from, until) +: acc, until, until + groupSize)
          }

          val (groups, newBuffer) = helper(List.empty, 0, groupSize)

          buffer = newBuffer

          groups
        }
      })


}

