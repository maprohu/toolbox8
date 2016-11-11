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

    val bbi = ByteBuffer.allocate(4)
    bbi.putInt(booId)
    bbi.flip()
    bbi.get(bytes, 0, 4)

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

  trait Registration {
    def unregister() : Unit
  }

  def register(
    id: Int,
    pickler: Pickler[Pickled]
  ) : Registration = {
    global.transform({ map =>
      map.updated(id, pickler)
    })

    new Registration {
      override def unregister(): Unit = {
        global.transform({ map =>
          map
            .get(id)
            .filter(_ eq pickler)
            .map(_ => map - id)
            .getOrElse(map)
        })
      }
    }


  }
}

object Ids {
  val Toolbox8JartreeAkka = 0
  val VoiceCore = 1

}
