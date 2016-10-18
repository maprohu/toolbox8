package toolbox8.akka.statemachine

import java.nio.ByteOrder

import akka.util.ByteString
import monix.execution.Scheduler.Implicits.global
import monix.reactive.subjects.PublishToOneSubject
import monix.reactive.{Consumer, Observable, Observer}
import toolbox8.akka.statemachine.DeepStream.State

import scala.concurrent.Future
import scala.io.StdIn
/**
  * Created by martonpapp on 17/10/16.
  */
object RunDeepStream {
  implicit val order = ByteOrder.BIG_ENDIAN

  def main(args: Array[String]): Unit = {
    Observable
      .range(1, 5)
      .map({ i =>
        Observable
          .range(0, i * 3)
          .map(j => ByteString(Array.fill[Byte](i.toInt * 10)(j.toByte)))
      })
      .map(DeepStream.encode)
      .flatten
//      .dump("x")
      .transform(DeepStream.decoder)
      .transform(DeepStream.strict)
      .dump("y")
      .subscribe()


    StdIn.readLine()


  }

//  import ByteStrings._
//  def state : Future[DeepStream.State] = {
//    val subject = PublishToOneSubject[ByteString]()
//
//    val s = State(
//      subject,
//      subject
//        .dump("s")
//        .map(_.size)
//        .sumL
//        .runAsync
//        .flatMap(i => state.map(s => (i, s)))
//        .map({
//          case (sum, st) =>
//            (
//              Observable(
//                ByteString
//                  .newBuilder
//                  .putInt(sum)
//                  .result()
//              ),
//              st
//            )
//        })
//    )
//
//    subject
//      .subscription
//      .map(_ => s)
//  }

}
