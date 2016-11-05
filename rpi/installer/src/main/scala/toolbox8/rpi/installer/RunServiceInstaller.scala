package toolbox8.rpi.installer

import toolbox8.modules.{JarTree8Modules, RpiModules, Toolbox8Modules}

/**
  * Created by martonpapp on 20/10/16.
  */
object RunServiceInstaller {

  val Target = Rpis.Home

  def main(args: Array[String]): Unit = {
//    import Rpis.MobileHomeWlan

    RpiService
      .upload(
        "voicer",
        JarTree8Modules.App,
        //        "toolbox8.jartree.app.JarTreeMain"
        "toolbox8.jartree.app.JarTreeAkkaMain",
        address = Target.host
      )(Target)

  }

}
