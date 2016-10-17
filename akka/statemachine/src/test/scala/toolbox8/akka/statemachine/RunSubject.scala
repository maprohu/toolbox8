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

    val subject = PublishSubject[Int]()

    val c = Consumer.foldLeft[Int, Int](0)(_ + _)
//    subject
//      .map(_ * 2)
//      .dump("x")
//      .sumL
//      .runAsync
//      .foreach(println)
//
//    subject
//      .runWith(c)
//      .runAsync
//      .foreach(println)

//    Observable
//      .range(0, 100)
//      .map(_.toInt)
//      .runWith(c)
//      .runAsync
//      .foreach(println)

//    Observable.concat(
//      subject
//    )
//      .dump("x")
//        .countL
//        .runAsync
//        .foreach(println)

//    val t = Task.create[Int]( { (sch, cb) =>
//      val s = new Subscriber[Int] {
//        override implicit def scheduler: Scheduler = global
//        var x = 0
//        override def onNext(elem: Int): Future[Ack] = {
//          x += elem
//          Continue
//        }
//        override def onError(ex: Throwable): Unit = ()
//        override def onComplete(): Unit = {
//          cb.onSuccess(x)
//        }
//      }
//      subject
//        .subscribe(
//          s
//        )
//
//
//      Cancelable.empty
//    })
//
//    val f = t.runAsync
//
//    Thread.sleep(500)
//      .dump("x")
//      .runWith(c)
//      .runAsync
//      .foreach(println)

//      .runAsync
//      .subscribe()

    val p = Promise[Int]()
    val (s, cc) =
      c.createSubscriber(
        Callback.fromPromise(p),
        global
      )

    subject
      .subscribe(
        s
      )
    CancelableFuture.apply(p.future, cc)


//    Thread.sleep(500)

    subject.onNext(1)
    subject.onComplete()


//    f.foreach(println)

    StdIn.readLine()



  }

}
