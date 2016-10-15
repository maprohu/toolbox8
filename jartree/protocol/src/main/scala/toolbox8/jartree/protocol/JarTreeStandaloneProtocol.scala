package toolbox8.jartree.protocol

import monix.reactive.Observable
import monix.reactive.observers.Subscriber
import sun.plugin2.message.Message
import toolbox8.jartree.standaloneapi.ByteArray

/**
  * Created by martonpapp on 15/10/16.
  */
object JarTreeStandaloneProtocol {

  object Framing {
    val MaxSize = 1024 * 32

    def check(size: Int) = {
      require(size <= MaxSize, "maximum package size exceeded")
    }

    case class Decoding(
      remaining: Int = 0,
      bufferSize: Int = 0,
      buffered: Stream[Byte] = Stream.empty,
      output: List[Stream[Byte]] = List.empty
    ) {
      def start : Decoding = {
        if (bufferSize >= 2) {
          val (header, data) = buffered.splitAt(2)
          val nextSize = ((header(0) & 0xFF) << 8) | (header(1) & 0xFF)
          if (nextSize == 0) {
            Decoding(
              0,
              bufferSize - 2,
              data,
              Stream.empty +: output
            ).start
          } else {
            check(nextSize)

            Decoding(
              nextSize,
              bufferSize - 2,
              data,
              output
            ).extract
          }
        } else {
          this
        }
      }

      def extract : Decoding = {
        if (remaining == 0) {
          start
        } else if (remaining <= bufferSize) {
          val (out, keep) = buffered.splitAt(remaining)

          Decoding(
            remaining = 0,
            bufferSize = bufferSize - remaining,
            buffered = keep,
            output = out +: output
          ).start
        } else {
          this
        }
      }

      def :+(elem: ByteArrayImpl) : Decoding = {
        Decoding(
          remaining = remaining,
          bufferSize = bufferSize + elem.count,
          buffered = buffered ++ elem.toSeq,
          output = List.empty
        ).extract
      }
    }

    object Decoding {
      val Empty = Decoding()
    }

    val Decoder : Observable[ByteArray] => Observable[Stream[Byte]] = { o =>
      o
        .filter(_.count() != 0)
        .scan(Decoding.Empty)(_ :+ _)
        .flatMap(d => Observable.fromIterable(d.output.reverse))
    }

    val Encoder : Observable[ByteArray] => Observable[ByteArray] = { o =>
      o
        .flatMap({ ba =>
          val size = ba.count()
          check(size)
          Observable[ByteArray](
            ByteArrayImpl(
              Array(
                ((size >> 8) & 0xff).toByte,
                (size & 0xff).toByte
              )
            ),
            ba
          )
        })
    }

  }

  object Multiplex {

    final case class Message(
      header: Byte,
      data: Array[Byte]
    )


    def demultiplex(
      o: Observable[Stream[Byte]],
      routes: Seq[Subscriber[Message]],
      fn: Byte => Int = _.toInt
    ) = {
      o
        .map(bs => Message(bs.head, bs.tail.toArray))
        .groupBy({ bs =>
          fn(bs.header)
        })
        .foreach({ g =>
          g
            .subscribe(routes(g.key))
        })
    }

    def multiplex[T](
      os : Seq[Observable[T]],
      fn: T => Message
    ) = {
      Observable
        .merge(
          os
            .map({ o =>
              o
                .map(fn)
            }):_*
        )
        .flatMap({ m =>
          Observable(
            Array(m.header),
            m.data
          )
        })
    }

    def multiplex(
      os : Seq[Observable[Array[Byte]]]
    ) = {
      multiplex[Message](
        os
          .zipWithIndex
          .map({
            case (o, idx) =>
              o
                .map({ ba =>
                  Message(idx.toByte, ba)
                })

          }),
        identity
      )

    }





    object Management {

      sealed trait Layer {
        lazy val header : Byte = Layers.indexOf(this).toByte
      }
      case object Data extends Layer
      case object Management extends Layer

      val Layers = Seq(
        Data,
        Management
      )

      def demultiplex(
        o: Observable[Stream[Byte]],
        data: Subscriber[Message],
        management: Subscriber[Message]
      ) = {
        Multiplex.demultiplex(
          o,
          Layers.map({
            case Data => data
            case Management => management
          })
        )
      }

      def multiplex(
        data: Observable[Array[Byte]],
        management: Observable[Array[Byte]]
      ) = {
        Multiplex.multiplex(
          Layers
            .map({
              case Data => data
              case Management => management
            })
        )
      }


    }


  }



}
