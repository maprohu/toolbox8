package toolbox8.jartree.testing

import toolbox8.jartree.standalone.JarTreeStandalone
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by martonpapp on 15/10/16.
  */
object RunJarTreeStandalone {

  def main(args: Array[String]): Unit = {

    JarTreeStandalone.run(
      port = 9978
    ).onComplete(println)


  }

}
