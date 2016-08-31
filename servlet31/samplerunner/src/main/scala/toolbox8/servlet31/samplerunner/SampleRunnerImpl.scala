package toolbox8.servlet31.samplerunner

import toolbox8.servlet31.runapi.{Servlet31Context, Servlet31Runner}

/**
  * Created by pappmar on 30/08/2016.
  */
class SampleRunnerImpl extends Servlet31Runner {
  override def run(context: Servlet31Context): Unit = {
    println("booooooooo")
  }
}
