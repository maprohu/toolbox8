package toolbox8.modules

import maven.modules.builder.{ScalaModule, SubModuleContainer}
import toolbox6.modules.{JarTreeModules, Toolbox6Modules}

/**
  * Created by martonpapp on 31/08/16.
  */
object JarTree8Modules {

  implicit val Container = SubModuleContainer(Toolbox8Modules.Root, "jartree")


  object Protocol extends ScalaModule(
    "protocol",
    mvn.`com.typesafe.akka:akka-stream_2.11:2.4.11`,
    mvn.`io.monix:monix_2.11:jar:2.0.4`,
    StandaloneApi,
    JarTreeModules.Util
  )

  object StandaloneApi extends ScalaModule(
    "standaloneapi",
    Toolbox6Modules.JavaApi,
    JarTreeModules.Api,
    mvn.`org.reactivestreams:reactive-streams:jar:1.0.0`
  )

  object Standalone extends ScalaModule(
    "standalone",
    Toolbox6Modules.JavaImpl,
    JarTreeModules.Impl,
    StandaloneApi,
    mvn.`com.typesafe.akka:akka-stream_2.11:2.4.11`,
    Protocol
  )

  object Echo extends ScalaModule(
    "echo",
    StandaloneApi,
    JarTreeModules.Util,
    mvn.`io.monix:monix-reactive_2.11:jar:2.0.4`,
    Toolbox6Modules.JavaImpl
  )

  object Client extends ScalaModule(
    "client",
    JarTreeModules.Client,
    JarTreeModules.Packaging,
    Protocol,
    mvn.`me.chrons:boopickle_2.11:jar:1.2.4`
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
    Standalone,
    Toolbox8Modules,
    Echo,
    JarTreeModules.Packaging
  )



}
