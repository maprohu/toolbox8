package toolbox8.modules

import mvnmod.builder.{Scala212Module, ScalaModule, SubModuleContainer}
import mvnmod.modules.MvnmodModules

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

  object Testing extends Scala212Module(
    "testing",
    mvn.`org.apache.tomcat.embed:tomcat-embed-core:jar:8.5.11`,
    mvn.`org.apache.tomcat.embed:tomcat-embed-jasper:jar:8.5.11`,
    mvn.`org.apache.tomcat.embed:tomcat-embed-websocket:jar:8.5.11`,
    mvn.`com.typesafe.akka:akka-http_2.12:jar:10.0.4`
  )

  object JarServlet extends Scala212Module(
    "jarservlet",
    mvn.`javax.servlet:javax.servlet-api:jar:3.1.0`,
    mvn.`javax.websocket:javax.websocket-api:jar:1.1`
  )

}
