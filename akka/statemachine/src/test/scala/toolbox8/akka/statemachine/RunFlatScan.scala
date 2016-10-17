package toolbox8.akka.statemachine

import monix.reactive.Observable

import scala.io.StdIn

/**
  * Created by pappmar on 17/10/2016.
  */
object RunFlatScan {
  def main(args: Array[String]): Unit = {
    import monix.execution.Scheduler.Implicits.global

    Observable
      .range(0, 100)
      .flatScan(0L)({ (acc, elem) =>
        println(s"$acc - $elem")
        Observable(
          acc + elem,
          acc + elem + 1
        )
      })
      .dump("x")
      .subscribe()


    StdIn.readLine()

  }

}
