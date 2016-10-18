package toolbox8.akka.statemachine

import java.nio.{ByteBuffer, ByteOrder}

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.{BidiFlow, Flow, Framing, Source}
import akka.util.ByteString
import boopickle.{PickleState, Pickler}

import scala.collection.immutable._
import scala.concurrent.Future

/**
  * Created by pappmar on 18/10/2016.
  */
object AkkaStreamCoding {

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
      asByteString(
        Pickle(value)
          .toByteBuffers
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



//    def process(
//      flow: Flow[ByteString, ByteString, NotUsed]
//    ) : Flow[ByteString, ByteString, NotUsed] =
//      Flow[ByteString]
//        .map(getByteHeader)
//        .splitAfter({ elem =>
//          val (header, _) = elem
//
//          header match {
//            case Last =>
//              true
//            case Error =>
//              throw new Exception("terminal coding error")
//            case _ =>
//              false
//          }
//        })
//        .map({ case (header, data) => data })
//        .via(flow)
//        .map(bs => NonLastBS ++ bs)
//        .concat(Source.single(LastBS))
//        .concatSubstreams

    val concat : Flow[Data, ByteString, NotUsed] = {
      Flow[Data]
        .flatMapConcat({ data =>
          data
            .fold(ByteString.empty)(_ ++ _)
        })
    }


//    def concat(implicit
//      materializer: Materializer
//    ) = {
//      Flow[ByteString]
//        .prefixAndTail(0)
//        .flatMapConcat({
//          case (_, source) =>
//            source
//              .fold(ByteString.empty)(_ ++ _)
//        })
//    }

  }

  object Multiplex {

    def flow(
      flows: Flow[ByteString, ByteString, Any]*
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
        .prefixAndTail(1)
        .flatMapConcat({
          case (Seq((header, emptyFirstData)), data) =>
            val headerBS = ByteString(header)

            data
              .map({ case (header, data) => data })
              .via(flows(header))
              .map(bs => headerBS ++ bs)
        })
        .mergeSubstreams
    }
  }


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
      import akka.stream.impl.fusing.Hacking._
//      Source
//        .un

      Flow[Data]
        .scanAsync(init)({ case (state, data) => state.next(data) })
        .flatMapConcat(_.out)
    }

    lazy val End : State = State(
      out = Source.maybe[Data],
      next = _ => Future.successful(End)
    )


    def sequence(
      steps: Seq[Data => StateOut],
      andThen: Transition
    ) : Transition = {
      steps match {
        case head +: tail =>
          { data =>
            Future.successful(
              State(
                head(data),
                sequence(
                  tail,
                  andThen
                )
              )
            )
          }
        case _ => // no more steps
          andThen
      }

    }



  }

  object Implicits {
    def asByteString(bbs: collection.Iterable[ByteBuffer]) : ByteString = {
      bbs
        .map(ByteString.apply)
        .foldLeft(ByteString.empty)(_ ++ _)
    }
    implicit class ByteBuffersOps(bbs: collection.Iterable[ByteBuffer]) {
      def asByteString : ByteString = {
        Implicits.asByteString(bbs)
      }
    }
  }

}
