package toolbox8.dummy

/**
  * Created by martonpapp on 19/10/16.
  */
object DummyMain {

  def main(args: Array[String]): Unit = {
    println("dummy started")

    Runtime.getRuntime.addShutdownHook(
      new Thread() {
        override def run(): Unit = {
          println("dummy stopped")
        }
      }
    )

    Thread.sleep(Long.MaxValue)
  }

}
