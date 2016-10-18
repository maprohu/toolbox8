package toolbox8.akka.statemachine

import akka.util.ByteString
import monix.execution.Scheduler.Implicits.global
import monix.reactive.{Observable, Observer}

import scala.io.StdIn

/**
  * Created by martonpapp on 17/10/16.
  */
object RunSubject2 {

  def main(args: Array[String]): Unit = {


    val s = DeepStream.newFState


    s
      .out
      .get
      .dump("x")
      .subscribe

    Observer.feed(
      s.observer,
      Iterator
        .continually(
          ByteString(1, 2, 3)
        )
    )

    StdIn.readLine()



  }

}
