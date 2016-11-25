package toolbox8.jartree.testing

import toolbox8.jartree.client.JarTreeStandaloneClient
import toolbox8.rpi.installer.Rpis

/**
  * Created by maprohu on 28-10-2016.
  */
object RunJarTreeStandaloneQuery {

//  val Target = Rpis.Localhost
  val Target = Rpis.Home

  def main(args: Array[String]): Unit = {
    JarTreeStandaloneClient
      .target(
        Target.host,
        Target.servicePort
      )
      .runQuery()


  }

}
