package toolbox8.modules

import mvnmod.builder.Module.ConfiguredModule
import mvnmod.builder.{ScalaModule, SubModuleContainer}
import toolbox6.modules.{JarTreeModules, Toolbox6Modules}

/**
  * Created by martonpapp on 31/08/16.
  */
object JarTree8Modules {

  implicit val Container = SubModuleContainer(Toolbox8Modules.Root, "jartree")

  object Common extends ScalaModule(
    "common"
  )

//  object Akka extends ScalaModule(
//    "akka",
//    Common,
//    Akka8Modules.Actor,
//    Akka8Modules.Stream,
//    mvn.`com.typesafe.akka:akka-remote_2.11:jar:2.4.12`,
//    mvn.`com.typesafe.akka:akka-persistence_2.11:jar:2.4.12`,
//    mvn.`org.iq80.leveldb:leveldb:jar:0.9`,
//    mvn.`org.iq80.leveldb:leveldb-api:jar:0.9`,
//    mvn.`org.fusesource.leveldbjni:leveldbjni-all:jar:1.8`
//  )
//
//  object Protocol extends ScalaModule(
//    "protocol",
//    StandaloneApi,
//    JarTreeModules.Util,
//    Akka8Modules.StateMachine,
//    mvn.`com.typesafe.akka:akka-stream_2.11:jar:2.4.12`,
//    mvn.`io.monix:monix_2.11:jar:2.0.6`
//  )

//  object StandaloneApi extends ScalaModule(
//    "standaloneapi",
//    JarTreeModules.Api,
//    mvn.`com.typesafe.akka:akka-stream_2.11:jar:2.4.12`
//  )
//
//  object Standalone extends ScalaModule(
//    "standalone",
//    JarTreeModules.Impl,
//    StandaloneApi,
//    Protocol,
//    Util,
//    Toolbox6Modules.StateMachine,
//    Akka8Modules.StateMachine,
//    Akka8Modules.Stream,
//    mvn.`com.typesafe.akka:akka-stream_2.11:jar:2.4.12`,
//    mvn.`com.typesafe.akka:akka-remote_2.11:jar:2.4.12`
//  )
//
//  object Util extends ScalaModule(
//    "util",
//    StandaloneApi,
//    mvn.`io.monix:monix-reactive_2.11:jar:2.0.6`
//  )

//  object Echo extends ScalaModule(
//    "echo",
//    StandaloneApi,
//    JarTreeModules.Util,
//    Akka,
//    mvn.`io.monix:monix-reactive_2.11:jar:2.0.6`
//  )

  object Client extends ScalaModule(
    "client",
    Common,
    JarTreeModules.Client
//    JarTreeModules.Packaging,
//    Protocol,
//    Toolbox6Modules.StateMachine,
//    Akka8Modules.StateMachine,
//    Akka8Modules.Stream,
//    Extra8Modules.Client,
//    Akka,
//    mvn.`me.chrons:boopickle_2.11:jar:1.2.4`
  )

  object Installer extends ScalaModule(
    "installer",
    Toolbox8Modules.Modules,
    Packaging,
    mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.7.8`
  )

  object Packaging extends ScalaModule(
    "packaging",
    JarTreeModules.Packaging
  )

//  object App extends ScalaModule(
//    "app",
//    Akka,
//    mvn.`ch.qos.logback:logback-classic:jar:1.1.7`
//  )

  object Logging extends ScalaModule(
    "logging",
    Toolbox6Modules.Logging,
    mvn.`ch.qos.logback:logback-classic:jar:1.1.7`
  )

  object StreamApp extends ScalaModule(
    "streamapp",
    Common,
    Logging,
    JarTreeModules.Common,
    mvn.`commons-io:commons-io:jar:2.5`,
    mvn.`io.monix:monix-execution_2.11:jar:2.1.1`
  )

  object Testing extends ScalaModule(
    "testing",
//    Standalone,
    Toolbox8Modules.Modules,
//    Echo,
    JarTreeModules.Packaging,
    Client,
    RpiModules.Installer,
//    Extra8Modules.Server,
//    App,
//    Akka8Modules.Actor,
//    Akka,
    StreamApp
  )



}
