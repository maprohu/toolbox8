package toolbox8.modules

import maven.modules.builder.{ScalaModule, SubModuleContainer}
import toolbox6.modules.JarTreeModules

/**
  * Created by martonpapp on 31/08/16.
  */
object JarTree8Modules {

  implicit val Container = SubModuleContainer(Toolbox8Modules.Root, "jartree")


  object Standalone extends ScalaModule(
    "standalone",
    JarTreeModules.Api
  )

  object Testing extends ScalaModule(
    "testing",
    Standalone
  )


}
