package toolbox8.modules

import maven.modules.builder.{ScalaModule, SubModuleContainer}

/**
  * Created by martonpapp on 31/08/16.
  */
object Akka8Modules {

  implicit val Container = SubModuleContainer(Toolbox8Modules.Root, "akka")


  object Stream extends ScalaModule(
    "stream",
    mvn.`com.typesafe.akka:akka-stream_2.11:2.4.9`
  )

}
