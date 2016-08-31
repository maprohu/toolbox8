package toolbox8.akka.stream

import akka.stream.scaladsl.Flow

import scala.collection.immutable._
import scala.concurrent.Future

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
  ) = {
    statefulMapAsyncConcat[State[In, Out], In, Out](
      initial,
      (state, in) => state(in)
    )
  }

  trait State[In, Out] {

    def apply(in: In) : Future[(State[In, Out], Out)]

  }

}

