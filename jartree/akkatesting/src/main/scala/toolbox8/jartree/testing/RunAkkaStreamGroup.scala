package toolbox8.jartree.testing

import akka.stream.scaladsl.Source
import toolbox8.akka.stream.AkkaStreamTools

/**
  * Created by maprohu on 01-11-2016.
  */
object RunAkkaStreamGroup {

  def main(args: Array[String]): Unit = {
    import AkkaStreamTools.Debug._


    Source(1 to 100)
      .groupBy(Int.MaxValue, _ % 2)
      .take(2)
      .mergeSubstreams
      .runForeach(println)
      .onComplete(println)


  }

}
