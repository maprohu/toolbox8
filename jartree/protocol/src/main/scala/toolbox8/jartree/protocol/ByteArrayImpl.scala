package toolbox8.jartree.protocol

import java.nio.ByteBuffer

import toolbox8.jartree.standaloneapi.ByteArray

/**
  * Created by martonpapp on 15/10/16.
  */
case class ByteArrayImpl(
  bytes: Array[Byte],
  offset: Int,
  count: Int
) extends ByteArray {
  def isEmpty = count == 0

  def toSeq = bytes.view(offset, offset + count)
}

object ByteArrayImpl {
  def apply(
    bytes: Array[Byte]
  ) : ByteArrayImpl = {
    ByteArrayImpl(
      bytes,
      0,
      bytes.length
    )
  }

  val Empty = ByteArrayImpl(Array.emptyByteArray)

  def apply(
    buffer: ByteBuffer
  ) : ByteArrayImpl = {
    val data = Array.ofDim[Byte](buffer.remaining())
    buffer.get(data)
    apply(data)
  }


  implicit def apply(
    byteArray: ByteArray
  ) : ByteArrayImpl = {
    byteArray match {
      case b : ByteArrayImpl => b
      case o => ByteArrayImpl(o.bytes(), o.offset(), o.count())
    }
  }
}

