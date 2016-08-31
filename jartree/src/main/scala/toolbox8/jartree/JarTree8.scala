package toolbox8.jartree

import java.io.{InputStream, OutputStream}

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Framing, Sink, Source}
import akka.util.ByteString
import jartree.util.{CaseJarKey, RunRequestImpl}
import toolbox8.akka.stream.ByteStreams

import scala.util.{Failure, Success, Try}
import scala.pickling.binary._
import scala.collection.immutable._

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

  def exec(
    flow: Flow[ByteString, ByteString, NotUsed]
  ) = {
    ByteStreams.groupFirstBytes(4)
      .prefixAndTail(1)
      .flatMapConcat({})
    Flow[ByteString]
      .prefixAndTail(1)
    Sink.combine(
      FrameDecoder
        .to(
          Sink.head
        ),
      Flow[ByteString]
        .asy
    )



  }

  def takeBytes(n: Long) = {
    Flow[ByteString]
      .statefulMapConcat()
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
