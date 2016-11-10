package toolbox8.modules

import mvnmod.builder.{ScalaModule, SubModuleContainer}
import mvnmod.modules.MvnmodModules
import toolbox6.modules.{JarTreeModules, Toolbox6Modules}
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
    JarTree8Modules.Util,
    Toolbox6Modules.Pickling,
    JarTreeModules.Impl
  )
  object Client extends ScalaModule(
    "client",
    Shared,
    JarTree8Modules.Util,
    Toolbox6Modules.Pickling,
    MvnmodModules.Builder
  )

  object Hello extends ScalaModule(
    "hello",
    Shared,
    Akka8Modules.Stream,
    mvn.`org.iq80.leveldb:leveldb:jar:0.9`
  )


}
