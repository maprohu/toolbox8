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
    mvn.`com.typesafe.akka:akka-stream_2.11:2.4.12`,
    mvn.`io.monix:monix_2.11:jar:2.0.4`,
    StandaloneApi,
    JarTreeModules.Util.R1,
    Akka8Modules.StateMachine
  )

  object StandaloneApi extends ScalaModule(
    "standaloneapi",
    JarTreeModules.Api.R1,
    mvn.`com.typesafe.akka:akka-stream_2.11:jar:2.4.12`
  )

  object Standalone extends ScalaModule(
    "standalone",
//    Toolbox6Modules.JavaImpl,
    JarTreeModules.Impl.R1,
    StandaloneApi,
    mvn.`com.typesafe.akka:akka-stream_2.11:2.4.12`,
    Protocol,
    Util,
    Toolbox6Modules.StateMachine,
    Akka8Modules.StateMachine,
    Akka8Modules.Stream
  )

  object Util extends ScalaModule(
    "util",
    StandaloneApi,
    mvn.`io.monix:monix-reactive_2.11:jar:2.0.5`
//    Toolbox6Modules.JavaImpl
  )

  object Echo extends ScalaModule(
    "echo",
    StandaloneApi,
    JarTreeModules.Util.R1,
    mvn.`io.monix:monix-reactive_2.11:jar:2.0.5`
//    Toolbox6Modules.JavaImpl
  )

  object Client extends ScalaModule(
    "client",
    JarTreeModules.Client,
    JarTreeModules.Packaging,
    Protocol,
    mvn.`me.chrons:boopickle_2.11:jar:1.2.4`,
    Toolbox6Modules.StateMachine,
    Akka8Modules.StateMachine,
    Akka8Modules.Stream,
    Extra8Modules.Client
  )

  object Installer extends ScalaModule(
    "installer",
    mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.7.8`,
    Toolbox8Modules.Modules,
    Packaging
  )

  object Packaging extends ScalaModule(
    "packaging",
    JarTreeModules.Packaging
  )

  object App extends ScalaModule(
    "app",
    Standalone,
    mvn.`ch.qos.logback:logback-classic:jar:1.1.7`
  )

//  object Exec extends ScalaModule(
//    "exec",
//    Protocol
//  )


  object Testing extends ScalaModule(
    "testing",
    Standalone,
    Toolbox8Modules.Modules,
    Echo,
    JarTreeModules.Packaging,
    Client,
    RpiModules.Installer,
    Extra8Modules.Server,
    App
  )



}
