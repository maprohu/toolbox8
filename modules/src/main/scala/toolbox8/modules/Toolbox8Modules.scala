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
    Toolbox6Modules.Modules.R1
  )

  object Common extends ScalaModule(
    "common",
    mvn.`io.monix:monix-eval_2.11:jar:2.0.5`,
    Toolbox6Modules.Logging.R1
  )

  object JarTree extends ScalaModule(
    "jartree",
    Akka8Modules.Stream,
    mvn.`com.typesafe.akka:akka-stream_2.11:jar:2.4.9`,
    mvn.`org.scala-lang.modules:scala-pickling_2.11:jar:0.10.1`,
    mvn.`io.monix:monix_2.11:jar:2.0.2`
  )


  object Dummy extends ScalaModule(
    "dummy"
  )

  object Leveldb extends ScalaModule(
    "leveldb",
    mvn.`org.iq80.leveldb:leveldb:jar:0.9`,
    Toolbox6Modules.Logging.R1,
    mvn.`com.typesafe.akka:akka-actor_2.11:jar:2.4.12`,
    mvn.`io.monix:monix_2.11:jar:2.0.5`
  )


//  object Common extends ScalaModule(
//    "common",
//    "1.0.0-SNAPSHOT",
//    mvn.`com.lihaoyi:scalarx_2.11:jar:0.3.1`
//  )

//  object JarTree extends ScalaModule(
//    "jartree",
//    "1.0.0-SNAPSHOT",
//    mvn.`commons-io:commons-io:jar:2.5`,
//    mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api:jar:2.2.2`,
//    mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-spi:jar:2.2.2`,
//    mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven:jar:2.2.2`,
//    mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-spi-maven:jar:2.2.2`,
//    mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-maven:jar:2.2.2`,
//    mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-maven-archive:jar:2.2.2`
//  )



}
