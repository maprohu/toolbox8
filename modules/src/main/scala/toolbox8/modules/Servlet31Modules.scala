package toolbox8.modules

import maven.modules.builder.{JavaModule, ScalaModule, SubModuleContainer}

/**
  * Created by pappmar on 30/08/2016.
  */
object Servlet31Modules {

  implicit val Container = SubModuleContainer(Toolbox8Modules.Root, "servlet31")

  object SingleApi extends ScalaModule(
    "singleapi",
    mvn.`javax.servlet:javax.servlet-api:jar:3.1.0`
  )

  object RunApi extends JavaModule(
    "runapi",
    SingleApi
  )

  object Webapp extends ScalaModule(
    "webapp",
    SingleApi,
    RunApi,
    mvn.`com.typesafe.scala-logging:scala-logging_2.11:jar:3.4.0`,
    mvn.`com.lihaoyi:upickle_2.11:jar:0.4.2`,
    mvn.`org.scala-sbt:io_2.11:jar:1.0.0-M6`
  )

  object SampleRunner extends ScalaModule(
    "samplerunner",
    RunApi
  )

}
