package toolbox8.jartree.testing

import toolbox8.jartree.app.{JarTreeAkkaMain, JarTreeMain}

/**
  * Created by maprohu on 05-11-2016.
  */
object RunJarTreeAkka {


  def main(args: Array[String]): Unit = {
    JarTreeMain.configureLogging("jartree", true)

    JarTreeAkkaMain
      .run(
        "jartree",
        "192.168.10.122"
      )
  }


}
