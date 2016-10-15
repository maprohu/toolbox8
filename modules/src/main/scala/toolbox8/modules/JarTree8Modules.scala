package toolbox8.modules

import maven.modules.builder.{ScalaModule, SubModuleContainer}
import toolbox6.modules.JarTreeModules

/**
  * Created by martonpapp on 31/08/16.
  */
object JarTree8Modules {

  implicit val Container = SubModuleContainer(Toolbox8Modules.Root, "jartree")


  object StandaloneApi extends ScalaModule(
    "standaloneapi",
    mvn.`org.reactivestreams:reactive-streams:jar:1.0.0`
  )

  object Standalone extends ScalaModule(
    "standalone",
    JarTreeModules.Api,
    StandaloneApi,
    mvn.`com.typesafe.akka:akka-http-experimental_2.11:jar:2.4.11`
  )

  object Installer extends ScalaModule(
    "installer",
    mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.7.7`
  )

  object Packaging extends ScalaModule(
    "packaging"
  )

  object Testing extends ScalaModule(
    "testing",
    Standalone
  )


}