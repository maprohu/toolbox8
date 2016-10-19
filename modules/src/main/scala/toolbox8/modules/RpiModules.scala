package toolbox8.modules

import maven.modules.builder.{ScalaModule, SubModuleContainer}
import toolbox6.modules.Toolbox6Modules

/**
  * Created by martonpapp on 31/08/16.
  */
object RpiModules {

  implicit val Container = SubModuleContainer(Toolbox8Modules.Root, "rpi")


  object Installer extends ScalaModule(
    "installer",
    mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.7.7`,
    mvn.`com.jcraft:jsch:jar:0.1.54`,
    JarTree8Modules.Packaging,
    Toolbox8Modules
  )

  object Remote extends ScalaModule(
    "remote"
  )

}
