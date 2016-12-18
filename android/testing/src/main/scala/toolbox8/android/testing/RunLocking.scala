package toolbox8.android.testing

import java.io.{File, RandomAccessFile}
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.{Paths, StandardOpenOption}

import scala.io.StdIn

/**
  * Created by maprohu on 18-12-2016.
  */
object RunLocking {

  val fn = "../toolbox8/target/lock.txt"

  def main(args: Array[String]): Unit = {
    new File(fn).getParentFile.mkdirs()

    println("locking")

    val f1 = new RandomAccessFile(
      fn,
      "rw"
    )
    val ch = f1.getChannel
    ch.lock()

    println("locked")

    val data = "hello".getBytes
    ch.write(ByteBuffer.wrap(data))

    StdIn.readLine("enter")

  }

}

object RunRLocking {
  def main(args: Array[String]): Unit = {
    println("read locking")
    val ch = FileChannel.open(
      Paths.get(RunLocking.fn),
      StandardOpenOption.READ
    )
    ch.lock(0, Long.MaxValue, true)
    val buff = ByteBuffer.allocate(100)
    val read = ch.read(buff)
    println(s"read: ${read}")
    println(buff.array().toSeq)




  }
}
