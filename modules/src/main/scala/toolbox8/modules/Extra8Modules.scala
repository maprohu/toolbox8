package toolbox8.modules

import maven.modules.builder.{ScalaModule, SubModuleContainer}
import toolbox6.modules.Toolbox6Modules
import toolbox8.modules.JarTree8Modules.Protocol

/**
  * Created by martonpapp on 31/08/16.
  */
object Extra8Modules {

  implicit val Container = SubModuleContainer(JarTree8Modules.Container, "extra")


  object Shared extends ScalaModule(
    "shared",
    Protocol
  )
  object Server extends ScalaModule(
    "server",
    Shared,
    JarTree8Modules.Util
  )
  object Client extends ScalaModule(
    "client",
    Shared,
    JarTree8Modules.Util
  )
//  object Util extends ScalaModule(
//    "util",
//    Shared
//  )


}
