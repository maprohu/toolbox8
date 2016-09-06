package toolbox8.jartree

import java.io.{InputStream, OutputStream}

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Framing, Sink, Source}
import akka.util.ByteString
import jartree.util.{CaseJarKey, RunRequestImpl}
import toolbox8.akka.stream.Streams.ByteStringState
import toolbox8.akka.stream.{ByteStreams, Streams}

import scala.util.{Failure, Success, Try}
import scala.pickling.binary._
import scala.collection.immutable._
import scala.pickling.Unpickler

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
  import scala.pickling.static._
  import scala.pickling.shareNothing._
//  implicit val runRequestUnpickler = Unpickler.generate[RunRequestWithAttachments]

  def server : Flow[ByteString, ByteString, NotUsed] = {
    val RequestLength = Streams.takeInt({ size =>
      Streams.takeBytes(size)({ bytes =>
        val request = bytes.toArray.unpickle[RunRequestWithAttachments]

        println(request)

        Streams.ignoreByteString
      })
    })

    Streams.stateMachineMapAsyncConcat(RequestLength)
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

case class RunRequestAttachment(
  key: CaseJarKey,
  size: Long
)

case class RunRequestWithAttachments(
  request: RunRequestImpl,
  attachments: Seq[RunRequestAttachment]
)
