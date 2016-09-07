package toolbox8.jartree

import java.io.{InputStream, OutputStream}

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Framing, Sink, Source}
import akka.util.ByteString
import jartree.impl.JarTree
import jartree.util.{CaseJarKey, RunRequestImpl}
import toolbox8.akka.stream.Streams.ByteStringState
import toolbox8.akka.stream.{ByteStreams, Streams}

import scala.util.{Failure, Success, Try}
import scala.pickling.binary._
import scala.collection.immutable._
import scala.concurrent.{ExecutionContext, Promise}
import scala.pickling.{Pickler, Unpickler}

/**
  * Created by pappmar on 31/08/2016.
  */
object JarTree8 {

  val MaxMessageSize = Int.MaxValue / 4

  val FrameDecoder = Framing.simpleFramingProtocolDecoder(MaxMessageSize)

  def exec(
    in: InputStream,
    out: OutputStream
  ) : Try[Unit] = {
    ???
  }

//  def exec(
//    flow: Flow[ByteString, ByteString, NotUsed]
//  ) = {
//    ByteStreams.groupFirstBytes(4)
//      .prefixAndTail(1)
//      .flatMapConcat({})
//    Flow[ByteString]
//      .prefixAndTail(1)
//    Sink.combine(
//      FrameDecoder
//        .to(
//          Sink.head
//        ),
//      Flow[ByteString]
//        .asy
//    )
//
//
//
//  }
//

//  def takeBytes(n: Long, fn : ByteString => Unit) = {
//    Flow[ByteString]
//      .statefulMapConcat()
//  }

  import scala.pickling.Defaults._
  import scala.pickling.binary._
//  import scala.pickling.static._
  import scala.pickling.shareNothing._
//  implicit val runRequestUnpickler = Unpickler.generate[RunRequestWithAttachments]
//  implicit val requestPickler = Pickler.generate[RunRequestWithAttachments]

  def server(
    jarTree: JarTree
  )(implicit
    executionContext: ExecutionContext
  ) : Flow[ByteString, ByteString, NotUsed] = {
//    val RequestLength = Streams.takeInt({ size =>
//      Streams.takeBytes(size)({ bytes =>
//        val request = bytes.toArray.unpickle[RunRequestWithAttachments]
//
//        val attachmentPromises =
//          request
//            .attachments
//            .map({ att =>
//              val promise = Promise[InputStream]()
//
//              jarTree
//                .resolver
//                .cache
//                .put(
//                  att.key,
//                  promise.future
//                )
//
//              (att.size, promise)
//            })
//
//        val responseFuture = jarTree.runInternal(
//          request.request
//        )
//
//
//        println(request)
//
//        Streams.ignoreByteString
//      })
//    })

//    Flow[ByteString]
//      .via(
//        Streams.processFirstBytes(4)({ (first4Bytes, source) =>
//          val requestSize = Streams.toInt(first4Bytes.toArray)
//          source.via(
//            Streams.processFirstBytes(requestSize)({ (requestBytes, rest) =>
//              val request = requestBytes.toArray.unpickle[RunRequestWithAttachments]
//
//              println(request)
//
//              rest
//                .splitAfter()
//            })
//          )
//        })
//      )

    ???
  }

  def toBytes(int: Int) : Array[Byte] = {
    Array(
      ((int >> 24) & 0xff).toByte,
      ((int >> 16) & 0xff).toByte,
      ((int >> 8) & 0xff).toByte,
      ((int) & 0xff).toByte
    )
  }


  def client(request: RunRequestWithAttachments) : Flow[ByteString, ByteString, NotUsed] = {
    val bytes = request.pickle.value

    Flow[ByteString]
      .prepend(
        Source(
          Iterable(
            ByteString(toBytes(bytes.length)),
            ByteString(bytes)
          )
        )
      )

  }

}

trait Runner {
  def run: Flow[ByteString, ByteString, NotUsed]
}
