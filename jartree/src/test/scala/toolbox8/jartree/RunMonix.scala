package toolbox8.jartree

import monix.execution.cancelables.BooleanCancelable

/**
  * Created by pappmar on 07/09/2016.
  */
object RunMonix {

  def main(args: Array[String]): Unit = {

    val c = BooleanCancelable(() => println("boo"))

    c.cancel()
    c.cancel()


  }

}
