package toolbox8.akka.stream

import akka.stream.scaladsl.Source

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn

/**
  * Created by maprohu on 05-11-2016.
  */
object RunAkkaStream {

  def main(args: Array[String]): Unit = {

    import AkkaStreamTools.Debug._

    println(
      Await.result(
        Source
          .repeat()
          .zipWithIndex
          .take(100)
          .runForeach(println),
        Duration.Inf
      )
    )

    actorSystem.terminate()
  }

}
