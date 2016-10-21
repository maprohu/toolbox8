package toolbox8.jartree.protocol

import akka.stream.scaladsl.Framing
import toolbox6.jartree.api.JarPlugger
import toolbox6.jartree.util.ClassRequestImpl
import toolbox8.jartree.standaloneapi.{JarTreeStandaloneContext, Service}
import scala.collection.immutable._


object JarTreeStandaloneProtocol {


  object Management {

    type Plugger = JarPlugger[Service, JarTreeStandaloneContext]

    final case class VerifyRequest(
      ids: Seq[String]
    )

    final case class VerifyResponse(
      missing: Seq[Int]
    )

//    final case class PutHeader(
//      sizes: Seq[Long]
//    )

    final case class Plug(
      classRequest: ClassRequestImpl[Plugger]
//      param: Array[Byte]
    )

    case object Done

    final case class Error(
      reason: String
    )


  }

}




//import akka.util.ByteString
//import monix.execution.{Ack, Cancelable}
//import monix.reactive.{Observable, Observer}
//import monix.reactive.observers.Subscriber
//import monix.reactive.subjects.{PublishSubject, Subject}
//import toolbox6.jartree.api.JarPlugger
//import toolbox6.jartree.util.{CaseClassLoaderKey, ClassRequestImpl}
//import toolbox8.jartree.protocol.JarTreeStandaloneProtocol.Multiplex.{Layer, Message}
//import toolbox8.jartree.standaloneapi.{JarTreeStandaloneContext, Service}
//
//import scala.concurrent.Future
//
///**
//  * Created by martonpapp on 15/10/16.
//  */
//object JarTreeStandaloneProtocol {
//
//  object Framing {
//    val MaxSize = 1024 * 32
//
//    val Akka = akka.stream.scaladsl.Framing.simpleFramingProtocol(MaxSize)
//
//    def check(size: Int) = {
//      require(size <= MaxSize, "maximum package size exceeded")
//    }
//
//    case class Decoding(
//      remaining: Int = 0,
//      buffered: ByteString = ByteString.empty,
//      output: List[ByteString] = List.empty
//    ) {
//      def start : Decoding = {
//        if (buffered.size >= 2) {
//          val (header, data) = buffered.splitAt(2)
//          val nextSize = ((header(0) & 0xFF) << 8) | (header(1) & 0xFF)
//          if (nextSize == 0) {
//            Decoding(
//              0,
//              data,
//              ByteString.empty +: output
//            ).start
//          } else {
//            check(nextSize)
//
//            Decoding(
//              nextSize,
//              data,
//              output
//            ).extract
//          }
//        } else {
//          this
//        }
//      }
//
//      def extract : Decoding = {
//        if (remaining == 0) {
//          start
//        } else if (remaining <= buffered.size) {
//          val (out, keep) = buffered.splitAt(remaining)
//
//          Decoding(
//            remaining = 0,
//            buffered = keep,
//            output = out +: output
//          ).start
//        } else {
//          this
//        }
//      }
//
//      def :+(elem: ByteString) : Decoding = {
//        if (elem.isEmpty) {
//          this
//        } else {
//          Decoding(
//            remaining = remaining,
//            buffered = buffered ++ elem,
//            output = List.empty
//          ).extract
//        }
//      }
//    }
//
//    object Decoding {
//      val Empty = Decoding()
//    }
//
//    val Decoder : Observable[ByteString] => Observable[ByteString] = { o =>
//      o
//        .scan(Decoding.Empty)(_ :+ _)
//        .flatMap(d => Observable.fromIterable(d.output.reverse))
//    }
//
//    val Encoder : Observable[ByteString] => Observable[ByteString] = { o =>
//      o
//        .map({ ba =>
//          val size = ba.size
//          check(size)
//
//          ByteString(
//            ((size >> 8) & 0xff).toByte,
//            (size & 0xff).toByte
//          ) ++ ba
//        })
//    }
//
//  }
//
//  object Multiplex {
//
//    case class Layer(
//      headerCount: Int,
//      flow: Observable[Message] => Observable[Message]
//    )
//
//    final case class Message(
//      header: Int, // 1 Byte
//      data: ByteString
//    )
//
//    def connect(
//      layers: Seq[Layer]
//    ) : Observable[ByteString] => Observable[ByteString] = { o =>
//      val layersWithOffset =
//        layers
//          .zip(
//            layers
//              .init
//              .scanLeft(0)(_ + _.headerCount)
//          )
//
//      val headerToLayerIndex =
//        layers
//          .init
//          .zipWithIndex
//          .flatMap({
//            case (layer, idx) =>
//              Seq.fill(layer.headerCount)(idx)
//          }) :+ layers.size - 1
//
//      val lastLayerFirstHeader = headerToLayerIndex.size - 1
//
//      o
//        .map({ bs =>
//          val header = bs.head
//          val layerIdx = headerToLayerIndex(math.min(header, lastLayerFirstHeader))
//          (layerIdx, header, bs.tail)
//        })
//        .groupBy(_._1)
//        .mergeMap({ g =>
//          val (layer, offset) = layersWithOffset(g.key)
//
//          g
//            .map({
//              case (_, header, data) =>
//                Message(header - offset, data)
//            })
//            .transform(layer.flow)
//            .map(m => m.copy(header = m.header + offset))
//        })
//        .map({ m =>
//          ByteString(m.header.toByte) ++ m.data
//        })
//    }
//
//    val DropHeader : Observable[ByteString] => Observable[ByteString] = { o =>
//      o
//        .map(s => s.tail)
//    }
//
//
////    def demultiplex(
////      o: Observable[Iterable[Byte]],
////      routes: Seq[Subscriber[Message]],
////      fn: Byte => Int = _.toInt
////    ) = {
////      o
////        .map(bs => Message(bs.head, bs.tail.toArray))
////        .groupBy({ bs =>
////          fn(bs.header)
////        })
////        .foreach({ g =>
////          g
////            .subscribe(routes(g.key))
////        })
////    }
////
////    def multiplex[T](
////      os : Seq[Observable[T]],
////      fn: T => Message
////    ) = {
////      Observable
////        .merge(
////          os
////            .map({ o =>
////              o
////                .map(fn)
////            }):_*
////        )
////        .flatMap({ m =>
////          Observable(
////            Array(m.header),
////            m.data
////          )
////        })
////    }
////
////    def multiplex(
////      os : Seq[Observable[Array[Byte]]]
////    ) = {
////      multiplex[Message](
////        os
////          .zipWithIndex
////          .map({
////            case (o, idx) =>
////              o
////                .map({ ba =>
////                  Message(idx.toByte, ba)
////                })
////
////          }),
////        identity
////      )
////
////    }
//
//
//
//
//
//
//
//  }
//
//

//
//}
