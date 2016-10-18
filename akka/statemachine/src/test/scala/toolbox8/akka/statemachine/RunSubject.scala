package toolbox8.akka.statemachine

import monix.eval.{Callback, Task}
import monix.execution.Ack.Continue
import monix.execution.{Ack, Cancelable, CancelableFuture, Scheduler}
import monix.execution.Scheduler.Implicits.global
import monix.reactive.{Consumer, Observable}
import monix.reactive.observers.Subscriber
import monix.reactive.subjects.{PublishSubject, PublishToOneSubject}

import scala.concurrent.{Future, Promise}
import scala.io.StdIn

/**
  * Created by martonpapp on 17/10/16.
  */
object RunSubject {

  def main(args: Array[String]): Unit = {

    val subject = PublishToOneSubject[Int]()



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

    subject
      .dump("x")
      .map(_ + 1)
      .flatMap(i => Observable(i))
      .sumL
      .runAsync
      .foreach(println)

    subject
      .subscription
      .foreach({ _ =>
        subject.onNext(1)
        subject.onComplete()
      })

//    Thread.sleep(500)




    StdIn.readLine()



  }

}
