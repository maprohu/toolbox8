package toolbox8.modules

import mvnmod.builder.{ScalaModule, SubModuleContainer}


/**
  * Created by martonpapp on 31/08/16.
  */
object Common8Modules {

  implicit val Container = SubModuleContainer(Toolbox8Modules.Root, "common")


  object Tools extends ScalaModule(
    "tools",
    mvn.`com.typesafe.akka:akka-http-experimental_2.11:jar:2.4.9`,
    mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.7.7`
  )

}
