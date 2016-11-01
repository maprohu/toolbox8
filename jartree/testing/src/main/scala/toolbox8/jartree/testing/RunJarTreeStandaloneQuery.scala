package toolbox8.jartree.testing

import toolbox8.jartree.client.JarTreeStandaloneClient
import toolbox8.rpi.installer.Rpis

/**
  * Created by maprohu on 28-10-2016.
  */
object RunJarTreeStandaloneQuery {

  def main(args: Array[String]): Unit = {
    JarTreeStandaloneClient
      .runQuery(
        Rpis.Home.host
//        Rpis.MobileCable.host
//        Rpis.MobileHomeWlan.host
      )


  }

}
