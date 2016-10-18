package toolbox8.akka.statemachine

import akka.util.ByteString
import monix.eval.{Callback, Task}
import monix.execution.Ack.Continue
import monix.execution.{Ack, Cancelable, CancelableFuture, Scheduler}
import monix.execution.Scheduler.Implicits.global
import monix.reactive.{Consumer, Observable, OverflowStrategy}
import monix.reactive.observers.Subscriber
import monix.reactive.subjects.{PublishSubject, PublishToOneSubject}

import scala.concurrent.{Future, Promise}
import scala.io.StdIn

/**
  * Created by martonpapp on 17/10/16.
  */
object RunSubject {

  def main(args: Array[String]): Unit = {

//    val subject = PublishToOneSubject[Int]()

//    val fs = DeepStream.newFState

//    fs

    Observable
      .fromIterable(
        (1 to 2000).flatMap({ i =>
          Iterable(
            ByteString(0, i),
            ByteString(1, i)
          )
        })
      )
        .dump("in")
//      .asyncBoundary(OverflowStrategy.BackPressure(2))
      .transform(DeepStream.decoder)
        .dump("trf")
//        .asyncBoundary(OverflowStrategy.BackPressure(2))
      .flatMap({ s =>
        Observable
          .fromTask(
            s
              .runWith(DeepStream.concat)
          )
      })
//      .asyncBoundary(OverflowStrategy.BackPressure(2))
        .dump("out")
      .subscribe
//      .foreach({ s =>
//        s
//          .runWith(DeepStream.concat)
//          .runAsync
//          .foreach(println)
//      })

//    (1 to 200).foreach({ i =>
//      fs.observer.onNext(ByteString(0, i))
//      fs.observer.onNext(ByteString(1, i+1))
//    })
//    fs.observer.onComplete()



//    val c = Consumer.foldLeft[Int, Int](0)(_ + _)
//
//    val p = Promise[Int]()
//    val (s, cc) =
//      c.createSubscriber(
//        Callback.fromPromise(p),
//        global
//      )
//
//    subject
//      .subscribe(
//        s
//      )
//    CancelableFuture.apply(p.future, cc)

//    subject
//      .dump("x")
//      .map(_ + 1)
//      .flatMap(i => Observable(i))
//      .sumL
//      .runAsync
//      .foreach(println)
//
//    subject
//      .subscription
//      .foreach({ _ =>
//        subject.onNext(1)
//        subject.onComplete()
//      })

//    Thread.sleep(500)




    StdIn.readLine()



  }

}
