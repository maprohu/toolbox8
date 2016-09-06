package toolbox8.akka.stream

import akka.NotUsed
import akka.stream.javadsl.BidiFlow
import akka.stream.scaladsl.{Flow, Sink, Source}
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
    fn: (State, In) => Future[CompleteOr[(State, Seq[Out])]]
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
    fn: (State, In) => Future[CompleteOr[(State, Out)]]
  )(implicit
    executionContext: ExecutionContext
  ) = {
    Flow[In]
      .statefulMapConcat({ () =>
        val holder = new StateHolder(initial)

        { in =>
          Iterable((holder, in))
        }
      })
      .mapAsync(1)({
        case (holder, in) =>
          fn(holder.state, in)
            .map({ stateOutOpt =>
              stateOutOpt.map({
                case (state, out) =>
                  holder.state = state
                  out
              })
            })
      })
      .takeWhile(_.isDefined)
      .map(_.get)
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

  type CompleteOr[T] = Option[T]
  type StateResult[In, Out] = Future[CompleteOr[(State[In, Out], Out)]]
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
    Future.successful(Some((state, Seq())))
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
            Some((takeBytesNext(n - in.length, buffer ++ in, andThen), Seq()))
          )
        } else {
          val (taken, left) = in.splitAt(n)

          for {
            stateOutOpt1 <- andThen(buffer ++ taken)
            stateOutOpt2 <- {
              stateOutOpt1
                .map({
                  case (state1, out1) =>
                    state1(left)
                      .map({ soOpt =>
                        soOpt
                          .map({
                            case (state2, out2) =>
                              (state2, out1 ++ out2)
                          })
                      })
                })
                .getOrElse(
                  Future.successful(None)
                )
            }
          } yield {
            stateOutOpt2
          }
        }
      }
    }

  }


  def toInt(bytes: Array[Byte]) : Int = {
    (((((bytes(0) << 8) | (bytes(1) & 0xff)) << 8) | (bytes(2) & 0xff)) << 8) | (bytes(3) & 0xff)
  }

  def takeInt(
    andThen: Int => ByteStringStateResult
  )(implicit
    executionContext: ExecutionContext
  ) = {
    takeBytes(4)({ bs =>
      val int = bs.foldLeft(0)((int, b) => (int << 8) | (b & 0xff))

      andThen(int)
    })
  }

  def ignore[In, Out] : State[In, Seq[Out]] = {
    new State[In, Seq[Out]] {
      override def apply(in: In): StateResult[In, Seq[Out]] = this
    }
  }

  val ignoreByteString = ignore[ByteString, ByteString]

  def groupFirstBytes(
    n: Int
  )(implicit
    executionContext: ExecutionContext
  ) : Flow[ByteString, ByteString, NotUsed] = {
    stateMachineMapAsyncConcat(
      takeBytes(n)({ firstNBytes =>
        Future.successful(
          Some((NoopSeqState[ByteString], Seq(firstNBytes)))
        )
      })
    )
  }

  def processFirstBytes[M](
    n: Int
  )(
    fn: (ByteString, Source[ByteString, NotUsed]) => Unit
  )(implicit
    executionContext: ExecutionContext
  ) = {
    Flow[ByteString]
      .via(Streams.groupFirstBytes(n))
      .prefixAndTail(1)
      .to(
        Sink.foreach({
          case (Seq(firstBytes), source) =>
            fn(firstBytes, source)
        })
      )
  }

  def NoopSeqState[T] = new State[T, Seq[T]] {
    override def apply(in: T): StateResult[T, Seq[T]] = {
      Future.successful(Some(this, Seq(in)))
    }
  }

//  type NewSubStream[In, Out] = Flow[In, Out, Future[SubStreamProcessorState[In, Out]]]
//  type SubStreamProcessorElement[In, Out] = Either[NewSubStream[In, Out], In]
//  type SubStreamProcessorState[In, Out] = State[In, Seq[SubStreamProcessorElement[In, Out]]]
//
//  trait SubState {
//
//  }

  sealed trait SubStateItem[In, Out]

  case class NewSubStream[In, Out](
    flow: Flow[In, Out, _]
  ) extends SubStateItem[In, Out]

  case class SubStreamIn[In, Out](
    in: In
  ) extends SubStateItem[In, Out]

//
//  case class SubStreamEnd[In, Out]() extends SubStateItem[In, Out]
//
//  trait SubState[In, Out] {
//    def apply(in: In) : Seq[Future[SubStateItem[In, Out]]]
//  }

  type SubStateOut[In, Out] = Seq[Future[Seq[SubStateItem[In, Out]]]]
  type SubState[In, Out] = State[In, SubStateOut[In, Out]]
  trait SubNext[In, Out] {
    def next() : Flow[In, Out, SubNextFuture[In, Out]]
  }
  type SubNextFuture[In, Out] = Future[SubNext[In, Out]]

  def processSubStreams[In, Out](
    initial: SubState[In, Out]
  ) : Flow[In, Out, NotUsed] = {
//    def state(s: SubState[In, Out]) : SubState[In, Out] = new SubState[In, Out] {
//      override def apply(in: In): StateResult[In, Seq[Either[Flow[In, Out, Future[SubState[In, Out]]], In]]] = {
//        s
//          .apply(in)
//          .flatMap({ sfOpt =>
//            sfOpt
//              .map({
//                case (nextState, output) =>
//              })
//          })
//
//      }
//    }

    Flow[In]
      .via(
        stateMachineMapAsyncConcat(
          initial
        )
      )
      .mapAsync(1)(identity)
      .mapConcat(identity)
      .splitWhen(_.isInstanceOf[NewSubStream])
      .prefixAndTail(1)
      .flatMapConcat ({
        case (Seq(NewSubStream(stream)), source) =>
          source
            .map({
              case SubStreamIn(in) =>
                in
              case _ => ???
            })
            .via(stream)
        case _ => ???
      })
      .concatSubstreams
  }

  type ByteSubStateOut = SubStateOut[ByteString, ByteString]
  type ByteSubState = SubState[ByteString, ByteString]

  def subTakeBytes(
    n: Int,
    buffer: ByteString,
    next: SubNextFuture[ByteString, ByteString]
  ) : ByteSubState = {
    new State[ByteString, ByteSubStateOut] {
      override def apply(in: ByteString): StateResult[ByteString, ByteSubStateOut] = {
        if (n > in.length) {
          subTakeBytes(n - in.length, buffer ++ in, next)
        } else {
          val (taken, left) = in.splitAt(n)
          Seq(
            
            Future.successful(
              SubStreamIn(
                buffer ++ taken
              )
            ),
            next
              .flatMap({ sn =>
                Seq(

                )
              })

          )

          ???
        }

      }
    }

  }

//
//  def subTakeBytes[Out](
//    n: Long
//  )(
//    flow: NewSubStream[ByteString, Out]
//
//  )

}

