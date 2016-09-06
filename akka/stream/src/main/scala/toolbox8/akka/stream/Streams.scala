package toolbox8.akka.stream

import akka.NotUsed
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
    fn: (State, In) => Future[(State, Seq[Out])]
  )(implicit
    executionContext: ExecutionContext
  ) : Flow[In, Out, NotUsed] = {
    statefulMapAsync(
      initial,
      fn
    ).mapConcat(identity)
  }

  def statefulMapAsync[State, In, Out](
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
    initial: State[In, Seq[Out]]
  )(implicit
    executionContext: ExecutionContext
  ) : Flow[In, Out, NotUsed] = {
    statefulMapAsyncConcat[State[In, Seq[Out]], In, Out](
      initial,
      (state, in) => state(in)
    )
  }

  def stateMachineMapAsync[In, Out](
    initial: State[In, Out]
  )(implicit
    executionContext: ExecutionContext
  ) = {
    statefulMapAsync[State[In, Out], In, Out](
      initial,
      (state, in) => state(in)
    )
  }

  type StateResult[In, Out] = Future[(State[In, Out], Out)]
  trait State[In, Out] {
    def apply(in: In) : StateResult[In, Out]
  }
  type StateFunction[In, Out] = In => StateResult[In, Out]
  implicit def fn2state[In, Out](fn: StateFunction[In, Out]) = {
    new State[In, Out] {
      override def apply(in: In): StateResult[In, Out] = fn(in)
    }
  }
  implicit def concatState2result[In, Out](state: State[In, Seq[Out]]) : StateResult[In, Seq[Out]] = {
    Future.successful(state, Seq())
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

  type ByteStringState = State[ByteString, Seq[ByteString]]
  type ByteStringStateResult = StateResult[ByteString, Seq[ByteString]]
  type ByteStringStateFunction = StateFunction[ByteString, Seq[ByteString]]

  def takeBytes(
    n: Int
  )(
    andThen: ByteStringStateFunction
  )(implicit
    executionContext: ExecutionContext
  ) : ByteStringState = {
    takeBytesNext(n, ByteString(), andThen)
  }

  def takeBytes(
    n: Int,
    andThen: ByteStringState
  )(implicit
    executionContext: ExecutionContext
  ) : ByteStringState = {
    takeBytesNext(n, ByteString(), andThen)
  }

  def takeBytesNext(
    n: Int,
    buffer: ByteString,
    andThen: ByteStringState
  )(implicit
    executionContext: ExecutionContext
  ) : ByteStringState = {
    new State[ByteString, Seq[ByteString]] {
      override def apply(in: ByteString): ByteStringStateResult = {
        if (in.length < n) {
          Future.successful(
            (takeBytesNext(n - in.length, buffer ++ in, andThen), Seq())
          )
        } else {
          val (taken, left) = in.splitAt(n)

          for {
            (state1, out1) <- andThen(buffer ++ taken)
            (state2, out2) <- state1(left)
          } yield {
            (state2, out1 ++ out2)
          }
        }
      }
    }

  }

  def takeInt(
    andThen: Int => ByteStringStateResult
  )(implicit
    executionContext: ExecutionContext
  ) = {
    takeBytes(4)({ bs =>
      val int = bs.foldLeft(0)((int, b) => (int << 8) | b)

      andThen(int)
    })
  }

  def ignore[In, Out] : State[In, Seq[Out]] = {
    new State[In, Seq[Out]] {
      override def apply(in: In): StateResult[In, Seq[Out]] = this
    }
  }

  val ignoreByteString = ignore[ByteString, ByteString]

}

