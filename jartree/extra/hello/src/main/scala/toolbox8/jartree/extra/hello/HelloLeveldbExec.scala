package toolbox8.jartree.extra.hello

import java.io.File

import akka.stream.scaladsl.Flow
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import org.iq80.leveldb.impl.Iq80DBFactory._
import org.iq80.leveldb.Options
import toolbox8.akka.stream.Flows
import toolbox8.jartree.extra.shared.ExecProtocol.Executable
import toolbox8.jartree.extra.shared.HasStorageDir

/**
  * Created by maprohu on 02-11-2016.
  */
object HelloLeveldbExec extends LazyLogging {
  def run(dbDir: File) = {
    logger.info(s"hello db using: ${dbDir}")
    dbDir.mkdirs()
    val options = new Options
    options.createIfMissing(true)
    val db = factory.open(
      dbDir,
      options
    )
    try {
      db.put(
        bytes("hello"),
        bytes("bello")
      )

      asString(
        db.get(bytes("hello"))
      )
    } finally {
      db.close()
    }
  }


}
class HelloLeveldbExec extends Executable[HasStorageDir] {
  override def flow(ctx: HasStorageDir): Flow[ByteString, ByteString, _] = {
    Flows.stringResult {
      ctx
        .storageDir
        .map({ s =>
          HelloLeveldbExec.run(
            new File(s.toFile, "leveldb")
          )
        })
        .getOrElse("<no storage dir>")
    }
  }
}
