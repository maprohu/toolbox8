package toolbox8.akka.statemachine

import java.nio.ByteOrder

import akka.util.ByteString
import monix.execution.Scheduler.Implicits.global
import monix.reactive.subjects.PublishToOneSubject
import monix.reactive.{Consumer, Observable, Observer}
import toolbox8.akka.statemachine.DeepStream.State

import scala.io.StdIn
/**
  * Created by martonpapp on 17/10/16.
  */
object RunDeepStream {
  implicit val order = ByteOrder.BIG_ENDIAN

  def main(args: Array[String]): Unit = {

    Observable
      .range(1, 5)
      .flatMap({ i =>
        Observable
          .range(0, i * 3)
          .map(j => ByteString(Array.fill[Byte](i.toInt * 10)(j.toByte)))
          .transform(DeepStream.chunks)
      })
      .transform(
        DeepStream.stateMachine(
          Observable.empty,
          state
        )
      )
      .dump("x")
      .subscribe()

    StdIn.readLine()


  }

  import ByteStrings._
  def state : DeepStream.State = {
    val subject = PublishToOneSubject[ByteString]()


    State(
      subject,
      subject
        .map({ bs =>
          println(bs)
          bs.size
        })
        .consume(
          Consumer.foldLeft[Int, Int](0)(_ + _)
        )
        .map({ sum =>
          (
            Observable(
              ByteString
                .newBuilder
                .putInt(sum)
                .result()
            ),
            state
          )
        })
    )

  }

}
