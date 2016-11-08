package toolbox8.akka.actor

import java.nio.ByteBuffer

import akka.serialization.Serializer
import boopickle.Pickler
import monix.execution.atomic.Atomic

import scala.collection.immutable._

/**
  * Created by maprohu on 08-11-2016.
  */
class BoopickleSerializer extends Serializer {
  override def identifier: Int = 88537223
  override def includeManifest: Boolean = false

  override def toBinary(o: AnyRef): Array[Byte] = {
    val p = o.asInstanceOf[Pickled]

    import boopickle.DefaultBasic._

    val booId = p.booId

    implicit val pickler =
      BoopickleSerializer
        .global
        .get
        .apply(booId)

    val bb = Pickle[Pickled](p).toByteBuffer

    val bytes = Array.ofDim[Byte](4 + bb.remaining())

    ByteBuffer
      .allocate(4)
      .putInt(booId)
      .get(bytes, 0, 4)

    bb
      .get(bytes, 4, bb.remaining())

    bytes
  }

  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = {
    val bb = ByteBuffer.wrap(bytes)
    val booId = bb.getInt

    import boopickle.DefaultBasic._

    implicit val pickler =
      BoopickleSerializer
        .global
        .get
        .apply(booId)

    Unpickle[Pickled].fromBytes(bb)
  }
}

object BoopickleSerializer {
  val global = Atomic(Map.empty[Int, Pickler[Pickled]])

  type Picklers = Map[Int, Pickler[Pickled]]
}
