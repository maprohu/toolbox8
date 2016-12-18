package toolbox8.android.testing

import java.io.{File, RandomAccessFile}
import java.nio.channels.FileChannel
import java.nio.file.{Paths, StandardOpenOption}

/**
  * Created by maprohu on 18-12-2016.
  */
object RunReadLocking {

  def main(args: Array[String]): Unit = {
    val ch = FileChannel.open(
      Paths.get("nonexisting"),
      StandardOpenOption.READ
    )
    ch.lock(0, Long.MaxValue, true)


  }

}
