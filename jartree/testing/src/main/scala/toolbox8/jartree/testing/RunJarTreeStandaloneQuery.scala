package toolbox8.jartree.testing

import toolbox8.jartree.client.JarTreeStandaloneClient

/**
  * Created by maprohu on 28-10-2016.
  */
object RunJarTreeStandaloneQuery {

  def main(args: Array[String]): Unit = {
    JarTreeStandaloneClient
      .runQuery(
        "localhost",
        9721
      )


  }

}
