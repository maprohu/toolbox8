package toolbox8.jartree.protocol

import monix.reactive.Observable

import scala.io.StdIn
import scala.util.Random

/**
  * Created by martonpapp on 15/10/16.
  */
object RunProtocolTest {
  def main(args: Array[String]): Unit = {
    import monix.execution.Scheduler.Implicits.global

    val data =
      Iterator
        .continually(
          Iterator
            .continually(
              Random.nextInt().toByte
            )
            .take(
              Random.nextInt(
                10
              )
            )
            .toList
        )
        .take(30)
        .toList

    println(data)


    Observable
      .fromIterable(data)
      .map(_.toArray)
      .map(ByteArrayImpl.apply)
      .transform(JarTreeStandaloneProtocol.Framing.Encoder)
      .toListL
      .runAsync
      .foreach { list =>
        val flow =
          list
            .map(ByteArrayImpl.apply)
            .flatMap(_.toSeq)
        println(flow)

        Observable
          .fromIterator(
            flow
              .grouped(20)
              .map(_.toArray)
              .map(ByteArrayImpl.apply)
          )
          .transform(JarTreeStandaloneProtocol.Framing.Decoder)
          .map(_.toList)
          .toListL
          .runAsync
          .foreach { dec =>
            println(dec)
            require(data == dec)
          }


      }

    StdIn.readLine()

//    Observable
//      .repeat[ByteArray](ByteArrayImpl(Array.ofDim[Byte](10)))

  }

}
