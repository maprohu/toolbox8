package toolbox8.modules

import mvnmod.builder.{MavenCentralModule, RootModuleContainer, ScalaModule}
import mvnmod.modules.MvnmodModules
import toolbox6.modules.Toolbox6Modules

/**
  * Created by martonpapp on 29/08/16.
  */
object Toolbox8Modules {

  implicit val Root = RootModuleContainer("toolbox8")

  object Modules extends ScalaModule(
    "modules",
    MvnmodModules.Builder,
    Toolbox6Modules.Modules
  )

  object Common extends ScalaModule(
    "common",
    Toolbox6Modules.Logging.R2,
    mvn.`io.monix:monix-eval_2.11:jar:2.0.6`
  )

  object JarTree extends ScalaModule(
    "jartree",
//    Akka8Modules.Stream,
    mvn.`com.typesafe.akka:akka-stream_2.11:jar:2.4.9`,
    mvn.`org.scala-lang.modules:scala-pickling_2.11:jar:0.10.1`,
    mvn.`io.monix:monix_2.11:jar:2.0.2`
  )


  object Dummy extends ScalaModule(
    "dummy"
  )

  object Leveldb extends ScalaModule(
    "leveldb",
    Toolbox6Modules.Logging.R2,
    mvn.`org.iq80.leveldb:leveldb:jar:0.9`,
    mvn.`io.monix:monix-execution_2.11:jar:2.1.1`
  )

  object Installer extends ScalaModule(
    "installer",
    MvnmodModules.Common,
    mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.8.0`,
    mvn.`com.jcraft:jsch:jar:0.1.54`

  )

}
