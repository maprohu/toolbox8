package toolbox8.modules

import maven.modules.builder.{RootModuleContainer, ScalaModule}
import maven.modules.utils.MavenCentralModule

/**
  * Created by martonpapp on 29/08/16.
  */
object Toolbox8Modules extends MavenCentralModule(
  "toolbox8-modules",
  "toolbox8-modules",
  "1.0.0-SNAPSHOT"
) {

  implicit val Root = RootModuleContainer("toolbox8")

  object JarTree extends ScalaModule(
    "jartree",
    Akka8Modules.Stream,
    mvn.`com.typesafe.akka:akka-stream_2.11:2.4.9`,
    mvn.`org.scala-lang.modules:scala-pickling_2.11:jar:0.10.1`,
    mvn.`io.monix:monix_2.11:jar:2.0.2`
  )

  object Rpi extends ScalaModule(
    "rpi",
    mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.7.7`,
    mvn.`com.jcraft:jsch:jar:0.1.54`
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
