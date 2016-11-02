package toolbox8.leveldb

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import monix.execution.Cancelable
import org.iq80.leveldb.Options
import org.iq80.leveldb.impl.Iq80DBFactory._
import toolbox8.leveldb.LevelDB.{Bytes, Entry}

import scala.collection.AbstractIterator

/**
  * Created by maprohu on 02-11-2016.
  */
class LevelDB(
  dbDir: File
) extends LazyLogging {
  val db = {
    logger.info(s"hello db using: ${dbDir}")
    dbDir.mkdirs()
    val options = new Options
    options.createIfMissing(true)

    factory.open(
      dbDir,
      options
    )
  }



  def list[T](
    prefix: Key
  )(
    fn: Iterator[Entry] => T
  ) : T = {
    val it = db.iterator()
    it.seek(prefix.array)

    val it2 = new AbstractIterator[Entry] {
      override def hasNext: Boolean = {
        it.hasNext && it.peekNext().getKey.startsWith(prefix.seq)
      }

      override def next(): Entry = it.next()
    }

    try {
      fn(it2)
    } finally {
      it.close()
    }
  }

  val close = Cancelable(() => db.close())
}

object LevelDB {

  type Entry = java.util.Map.Entry[Array[Byte], Array[Byte]]
  type Bytes = Array[Byte]
  type Bytes4 = Bytes

  def apply(
    dbDir: File
  ): LevelDB = new LevelDB(dbDir)
}

case class Key(
  array: Array[Byte],
  seq: Seq[Byte]
)

object Key {
  implicit def fromArray(array: Bytes) = Key(array, array)
  implicit def fromSeq(seq: Seq[Byte]) = Key(Array(seq:_*), seq)
}

class Keys extends Enumeration {
  case class KeyVal(

  ) extends Val

}

