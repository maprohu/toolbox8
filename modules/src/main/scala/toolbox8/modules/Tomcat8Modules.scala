package toolbox8.modules

import mvnmod.builder.{ModuleId, Scala212Module, ScalaModule, SubModuleContainer}
import mvnmod.modules.MvnmodModules

import scala.collection.immutable._

/**
  * Created by martonpapp on 31/08/16.
  */
object Tomcat8Modules {

  implicit val Container = SubModuleContainer(Toolbox8Modules.Root, "tomcat")

  object Packaging extends ScalaModule(
    "packaging",
    Toolbox8Modules.Modules,
    MvnmodModules.Common,
    MvnmodModules.Builder,
    mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.8.2`
  )

  object Shared extends Scala212Module(
    "shared",
    mvn.`com.typesafe.akka:akka-http_2.12:jar:10.0.4`,
    mvn.`io.suzaku:boopickle_2.12:jar:1.2.6`
  )

  object JarServlet extends Scala212Module(
    "jarservlet",
    Shared,
    mvn.`io.monix:monix-eval_2.12:jar:2.2.3`,
    mvn.`javax.servlet:javax.servlet-api:jar:3.1.0`,
    mvn.`javax.websocket:javax.websocket-api:jar:1.1`,
    mvn.`com.typesafe.scala-logging:scala-logging_2.12:jar:3.5.0`,
    mvn.`ch.qos.logback:logback-classic:jar:1.1.7`
  )

  object TestApp extends Scala212Module(
    "testapp",
    JarServlet
  )

  object Clients extends Scala212Module(
    "clients",
    JarServlet,
    mvn.`org.scala-lang:scala-compiler:jar:2.12.1`
  )

  object Uploads extends Scala212Module(
    "uploads"
  )

  object Testing extends Scala212Module(
    "testing",
    JarServlet,
    TestApp,
    Toolbox8Modules.Modules.asModule.copy(excludes = Set(mvn.`org.scala-lang:scala-library:jar:2.11.8`.moduleId)),
    mvn.`org.apache.tomcat.embed:tomcat-embed-core:jar:8.5.11`,
    mvn.`org.apache.tomcat.embed:tomcat-embed-jasper:jar:8.5.11`,
    mvn.`org.apache.tomcat.embed:tomcat-embed-websocket:jar:8.5.11`
  )

}
