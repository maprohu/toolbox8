package toolbox8.rpi.installer

import toolbox8.modules.JarTree8Modules

/**
  * Created by martonpapp on 20/10/16.
  */
object RunStreamServiceInstaller {

//  val Target = Rpis.Home
  val Target = Rpis.MobileCable

  def main(args: Array[String]): Unit = {
//    import Rpis.MobileHomeWlan

    RpiService
      .upload(
        "voicer",
        JarTree8Modules.StreamApp,
        "toolbox8.jartree.streamapp.StreamAppMain",
        port = Target.servicePort
      )(Target)

  }

}
