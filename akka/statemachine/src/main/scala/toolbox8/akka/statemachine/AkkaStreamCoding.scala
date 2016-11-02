package toolbox8.akka.statemachine

import java.nio.{ByteBuffer, ByteOrder}

import akka.NotUsed
import akka.event.Logging
import akka.stream.{Attributes, Materializer, OverflowStrategy}
import akka.stream.scaladsl.{BidiFlow, Flow, Framing, Source, SourceQueueWithComplete}
import akka.util.ByteString
import boopickle.{PickleState, Pickler}
import com.typesafe.scalalogging.LazyLogging
import monix.execution.atomic.Atomic

import scala.collection.immutable._
import scala.concurrent.{ExecutionContext, Future, Promise}

/**
  * Created by pappmar on 18/10/2016.
  */
object AkkaStreamCoding extends LazyLogging {

  implicit class SourceImpl[O, M](flow: Source[O, M]) {
    def trf[O2, M2](fn: Source[O, M] => Source[O2, M2]) = fn(flow)
  }

  implicit class FlowImpl[I, O, M](flow: Flow[I, O, M]) {
    def trf[O2, M2](fn: Flow[I, O, M] => Flow[I, O2, M2]) : Flow[I, O2, M2] = fn(flow)
  }

  implicit val byteOrder = ByteOrder.BIG_ENDIAN

  def mapPrefix[T](bs: ByteString)(n: Int)(fn: ByteString => T) : (T, ByteString) = {
    (fn(bs), bs.drop(n))
  }

  def getByteHeader(bs: ByteString) : (Byte, ByteString) =
    mapPrefix(bs)(1)({ h =>
      h(0)
    })

  def getIntHeader(bs: ByteString) : (Int, ByteString) =
    mapPrefix(bs)(4)({ h =>
      (h(0) << 24) |
        ((h(1) & 0xff) << 16) |
        ((h(2) & 0xff) << 8) |
        (h(3) & 0xff)
    })

  def getLongHeader(bs: ByteString) : (Int, ByteString) =
    mapPrefix(bs)(8)({ h =>
      (h(0) << 56) |
        ((h(1) & 0xff) << 48) |
        ((h(2) & 0xff) << 40) |
        ((h(3) & 0xff) << 32) |
        ((h(4) & 0xff) << 24) |
        ((h(5) & 0xff) << 16) |
        ((h(6) & 0xff) << 8) |
        (h(7) & 0xff)
    })

  type Data = Source[ByteString, Any]


  def concat(data: Data)(implicit
    materializer: Materializer
  ) : Future[ByteString] = {
    data
      .runFold(ByteString.empty)(_ ++ _)
  }

  def mapConcat[T](data: Data)(fn: ByteString => T)(implicit
    materializer: Materializer
  ) : Source[T, NotUsed] = {
    import materializer.executionContext
    Source
      .fromFuture(
        concat(data)
          .map(fn)
      )
  }

  def unpickle[T](data: Data)(implicit
    materializer: Materializer,
    u: Pickler[T]
  ) : Future[T] = {
    import boopickle.Default._
    import materializer.executionContext
    concat(data)
      .map({ bs =>
        Unpickle[T].fromBytes(bs.asByteBuffer)
      })
  }

  def pickle[T](
    value: T
  )(implicit
    state: PickleState,
    p: Pickler[T]
  ) : Data = {
    import boopickle.Default._
    import Implicits._
    Source.single(
      ByteString(
        Pickle(value)
          .toByteBuffer
      )
    )
  }

  val framing = Framing.simpleFramingProtocol(Terminal.MaxChunkSize * 2)

  object Terminal {

    val MaxChunkSize = 16 * 1024
    val IdHeaderSize = 4
    val TermHeaderSize = 1
    val HeaderSize = IdHeaderSize + TermHeaderSize


    val NonLast : Byte = 0
    val Last : Byte = 1
    val Error : Byte = 2

    val NonLastBS = ByteString(NonLast)
    val LastBS = ByteString(Last)
    val ErrorBS = ByteString(Error)

    val decoder : Flow[ByteString, Data, NotUsed] =
      Flow[ByteString]
        .map(getByteHeader)
        .splitAfter({ elem =>
          val (header, _) = elem

          header match {
            case Last =>
              true
            case Error =>
              throw new Exception("terminal coding error")
            case _ =>
              false
          }
        })
        .map({ case (header, data) => data })
        .prefixAndTail(0)
        .map({ case (_, data) => data })
        .concatSubstreams

    val encoder : Flow[Data, ByteString, NotUsed] = {
      Flow[Data]
        .flatMapConcat({ data =>
          data
            .mapConcat(bs => bs.grouped(MaxChunkSize).to[Iterable])
            .map(bs => NonLastBS ++ bs)
            .concat(Source.single(LastBS))
        })

    }

    val bidi = BidiFlow.fromFlows(
      decoder,
      encoder
    )

    val concat : Flow[Data, ByteString, NotUsed] = {
      Flow[Data]
        .flatMapConcat({ data =>
          data
            .fold(ByteString.empty)(_ ++ _)
        })
    }

  }

  object Multiplex {

    def flow(
      flows: Flow[ByteString, ByteString, _]*
    ) : Flow[ByteString, ByteString, NotUsed] = {
      Flow[ByteString]
        .prepend(
          Source(
            flows
              .indices
              .map({ idx =>
                ByteString(idx.toByte)
              })
          )
        )
        .map(getByteHeader)
        .groupBy(flows.size, { case (header, data) => header })
        .buffer(1, OverflowStrategy.backpressure)
        .prefixAndTail(1)
        .flatMapConcat({
          case (Seq((header, emptyFirstData)), data) =>
            logger.info("setting up flow: {}", header)
            val headerBS = ByteString(header)

            data
              .map({ case (header, data) => data })
              .via(flows(header))
              .map(bs => headerBS ++ bs)
        })
        .mergeSubstreams
        .buffer(1, OverflowStrategy.backpressure)
    }
  }

//  object DynamicMultiplex {
//
//    def client(
//      data: Source[Flow[ByteString, ByteString, _], _]
//    )(implicit
//      materializer: Materializer
//    ) : Flow[ByteString, ByteString, NotUsed] = {
//      Flow[ByteString]
//        .prefixAndTail(0)
//        .flatMapConcat({
//          case (_, source) =>
//            Source
//              .queue[ByteString](0, OverflowStrategy.backpressure)
//              .mapMaterializedValue({ outQueue =>
//                val streams =
//                  Atomic(
//                    Map.empty[Long, SourceQueueWithComplete[ByteString]]
//                  )
//
//                source
//                  .mapAsync(1)({ bs =>
//                    val (id, bs2) = getLongHeader(bs)
//                    val (header, payload) = getByteHeader(bs2)
//
//                    def send =
//
//                    header match {
//                      case Terminal.NonLast =>
//                      case Terminal.Error =>
//                      case Terminal.Last =>
//                    }
//                  })
//              })
//        })
//
//    }
//
//  }



  object StateMachine {

    type StateOut = Source[Data, Any]
    type Transition = Data => Future[State]
    case class State(
      out: StateOut = Source.empty,
      next: Transition
    )

    def flow(
      init: State
    ) : Flow[Data, Data, NotUsed] = {

      Flow[Data]
        .scanAsync(init)({ case (state, data) => state.next(data) })
        .flatMapConcat(_.out)
    }

    lazy val EndNext : Transition = _ => Future.successful(End)
    lazy val End : State = State(
      out = Source.maybe[Data],
      next = EndNext
    )


    def sequence(
      data: Data,
      steps: Seq[Data => Future[StateOut]],
      andThen: Data => Future[State]
    )(implicit
      executionContext: ExecutionContext
    ) : Future[State] = {
      steps match {
        case head +: tail =>
          head(data).map( d =>
            State(
              d,
              { d2 =>
                sequence(
                  d2,
                  tail,
                  andThen
                )
              }
            )
          )
        case _ => // no more steps
          andThen(data)
      }
    }

    def sequenceIn(
      steps: Seq[Data => Future[Unit]],
      andThen: => Future[State]
    )(implicit
      executionContext: ExecutionContext
    ) : Future[State] = {
      steps match {
        case head +: tail =>
          Future.successful(
            State(
              next = { data =>
                head(data)
                  .flatMap({ _ =>
                    sequenceIn(tail, andThen)
                  })
              }
            )
          )
        case _ => // no more steps
          andThen
      }

    }

    def sequenceIn2(
      steps: Seq[Data => Future[Unit]],
      andThen: Transition
    )(implicit
      executionContext: ExecutionContext
    ) : Transition = {
      steps match {
        case head +: tail =>
        { data =>
          head(data)
            .map({ _ =>
              State(
                next = sequenceIn2(tail, andThen)
              )
            })
        }
        case _ => // no more steps
          andThen
      }

    }

    def sequenceInAndState(
      out: StateOut,
      steps: Seq[Data => Future[Unit]],
      andThen: () => State
    )(implicit
      executionContext: ExecutionContext
    ) : State = {
      steps match {
        case head +: tail =>
          State(
            out = out,
            next = { data =>
              head(data)
                .map({ _ =>
                  sequenceInAndState(
                    Source.empty,
                    tail,
                    andThen
                  )
                })
            }
          )
        case _ => // no more steps
          val s = andThen()

          s.copy(
            out = out.concat(s.out)
          )
      }

    }



  }

  object Implicits {
//    def asByteString(bbs: collection.Iterable[ByteBuffer]) : ByteString = {
//      bbs
//        .map(ByteString.apply)
//        .foldLeft(ByteString.empty)(_ ++ _)
//    }
    //    implicit class ByteBuffersOps(bbs: collection.Iterable[ByteBuffer]) {
    //      def asByteString : ByteString = {
    //        Implicits.asByteString(bbs)
    //      }
    //    }
    implicit class ByteBuffersOps(bbs: ByteBuffer) {
      def asByteString : ByteString = {
        ByteString(bbs)
      }
    }
  }

}
