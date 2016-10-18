package toolbox8.akka.statemachine

import java.nio.ByteOrder

import akka.NotUsed
import akka.stream.scaladsl.Source
import akka.util.ByteString
import monix.reactive.{Consumer, Observable}

/**
  * Created by pappmar on 18/10/2016.
  */
object StreamCoding {

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

  object Framing {
    val MaxFrameSize = Terminal.MaxChunkSize * 2

  }

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

    sealed trait Package
    case class Data(
      bs: ByteString
    ) extends Package
    case object Term extends Package

    val decoder : Observable[ByteString] => Observable[Observable[ByteString]] = { o =>
      o
        .map(getIntHeader)
        .groupBy({ case (id, data) => id })
        .map({ go =>
          go
            .map({
              case (id, data) =>
                data
            })
            .flatMap({ bs =>
              val (head, data) = getByteHeader(bs)

              head match {
                case NonLast =>
                  Observable(
                    Data(data)
                  )
                case Last =>
                  Observable(
                    Data(data),
                    Term
                  )
                case Error =>
                  throw new Exception("term decoding error")
              }
            })
            .takeWhile(_ != Term)
            .collect({ case Data(bs) => bs })
        })

    }

    val encoder : Observable[Observable[ByteString]] => Observable[ByteString] = { o =>
      o
        .zipWithIndex
        .mergeMap({
          case (s, id) =>
            s
              .flatMap({ o =>
                Observable
                  .fromIterator(
                    o
                      .grouped(MaxChunkSize)
                  )
              })
              .map({ bs =>
                NonLastBS ++ bs
              })
              .:+(LastBS)
              .map({ bs =>
                ByteString
                  .newBuilder
                  .putInt(id.toInt)
                  .result() ++ bs
              })
        })
    }

    val concat : Consumer[ByteString, ByteString] = Consumer.foldLeft(ByteString.empty)(_ ++ _)

    val strict : Observable[Observable[ByteString]] => Observable[ByteString] = { o =>
      o
        .flatMap({ bso =>
          Observable
            .fromTask(
              bso.runWith(concat)
            )
        })
    }

  }




}
