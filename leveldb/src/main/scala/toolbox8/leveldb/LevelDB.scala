package toolbox8.leveldb

import java.io.File
import java.nio.ByteBuffer

import com.typesafe.scalalogging.LazyLogging
import monix.execution.Cancelable
import org.iq80.leveldb.{DB, Options, WriteOptions}
import org.iq80.leveldb.impl.Iq80DBFactory._
import toolbox8.leveldb.LevelDBTable.Key

/**
  * Created by maprohu on 02-11-2016.
  */
class LevelDB(
  dbDir: File
) extends LazyLogging {
  val db = {
    logger.info(s"leveldb using: ${dbDir}")
    dbDir.mkdirs()
    val options = new Options
    options.createIfMissing(true)

    factory.open(
      dbDir,
      options
    )
  }

  def longTable(
    prefix: Array[Byte] = Array.emptyByteArray
  ) = table(
    LongSize,
    prefix
  )

  def table[T](
    keySize: KeySize[T],
    prefix: Array[Byte] = Array.emptyByteArray
  ) = new LevelDBTable[T](
    db,
    prefix,
    keySize
  )



//  def list[T](
//    prefix: Key
//  )(
//    fn: Iterator[Entry] => T
//  ) : T = {
//    val it = db.iterator()
//    it.seek(prefix.array)
//
//    val it2 = new AbstractIterator[Entry] {
//      override def hasNext: Boolean = {
//        it.hasNext && it.peekNext().getKey.startsWith(prefix.seq)
//      }
//
//      override def next(): Entry = it.next()
//    }
//
//    try {
//      fn(it2)
//    } finally {
//      it.close()
//    }
//  }

  val cancelable = Cancelable(() => db.close())
}

object LevelDB {

//  type Entry = java.util.Map.Entry[Array[Byte], Array[Byte]]
//  type Bytes = Array[Byte]
//  type Bytes4 = Bytes

  def apply(
    dbDir: File
  ): LevelDB = new LevelDB(dbDir)
}

//case class Key(
//  array: Array[Byte],
//  seq: Seq[Byte]
//)
//
//object Key {
//  implicit def fromArray(array: Bytes) = Key(array, array)
//  implicit def fromSeq(seq: Seq[Byte]) = Key(Array(seq:_*), seq)
//}
//
//class Keys extends Enumeration {
//  case class KeyVal(
//
//  ) extends Val
//
//}

sealed trait KeySize[T] {
  def size : Int
  def keyProvider(init: LevelDBTable.Key) : KeyProvider[T]
}

trait KeyProvider[T] {
  def next(bb: ByteBuffer) : T
}

object ByteSize extends KeySize[Byte] {
  override def size: Int = java.lang.Byte.BYTES
  override def keyProvider(init: Key): KeyProvider[Byte] =
    new KeyProvider[Byte] {
      var id : Byte = Option(init).map(bb => ByteBuffer.wrap(bb).get).getOrElse(0)
      override def next(bb: ByteBuffer): Byte = {
        id = (id + 1).toByte
        bb.put(id)
        id
      }
    }
}
object Shortsize extends KeySize[Short] {
  override def size: Int = java.lang.Short.BYTES
  override def keyProvider(init: Key): KeyProvider[Short] =
    new KeyProvider[Short] {
      var id : Short = Option(init).map(bb => ByteBuffer.wrap(bb).getShort).getOrElse(0)
      override def next(bb: ByteBuffer): Short = {
        id = (id + 1).toShort
        bb.putShort(id)
        id
      }
    }
}
object IntSize extends KeySize[Int] {
  override def size: Int = java.lang.Integer.BYTES
  override def keyProvider(init: Key): KeyProvider[Int] =
    new KeyProvider[Int] {
      var id : Int = Option(init).map(bb => ByteBuffer.wrap(bb).getInt).getOrElse(0)
      override def next(bb: ByteBuffer): Int = {
        id += 1
        bb.putInt(id)
        id
      }
    }

  def toArray(v: Int) = {
    ByteBuffer.allocate(size).putInt(v).array()
  }

}
object LongSize extends KeySize[Long] {
  override def size: Int = java.lang.Long.BYTES
  override def keyProvider(init: Key): KeyProvider[Long] =
    new KeyProvider[Long] {
      var id : Long = Option(init).map(bb => ByteBuffer.wrap(bb).getLong).getOrElse(0)
      override def next(bb: ByteBuffer): Long = {
        id += 1
        bb.putLong(id)
        id
      }
    }
}

object LevelDBTable {
  type Key = Array[Byte]

  val KeyWriteOptions =
    new WriteOptions()
      .sync(false)
      .snapshot(false)

  val InsertWriteOptions =
    KeyWriteOptions

}

class LevelDBTable[T](
  db: DB,
  prefix: Array[Byte],
  keySize: KeySize[T]
) {
  import LevelDBTable._

  def allocateKeyBuffer = {
    val keyBuffer =
      ByteBuffer
        .allocate(prefix.length + keySize.size)
    keyBuffer.put(prefix)
    keyBuffer
  }

  val metaKey = {
    val keyBuffer = allocateKeyBuffer
    keyBuffer.put(Array.fill[Byte](keySize.size)(0))
    keyBuffer.array()
  }

  val keyProvider = {
    val key = db.get(metaKey)
    keySize.keyProvider(key)
  }

  def insert(
    data: Array[Byte],
    options : WriteOptions = InsertWriteOptions
  ) : T = {
    val keyBuffer = allocateKeyBuffer

    val id = keyProvider.synchronized {
      val id = keyProvider.next(keyBuffer)
      db.put(
        metaKey,
        keyBuffer.array().drop(prefix.length),
        KeyWriteOptions
      )
      id
    }

    db.put(
      keyBuffer.array(),
      data,
      options
    )

    id
  }




}