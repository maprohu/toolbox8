package toolbox8.modules

import maven.modules.builder.{ScalaModule, SubModuleContainer}
import toolbox6.modules.Toolbox6Modules

/**
  * Created by martonpapp on 31/08/16.
  */
object Akka8Modules {

  implicit val Container = SubModuleContainer(Toolbox8Modules.Root, "akka")


  object StateMachine extends ScalaModule(
    "statemachine",
    Toolbox6Modules.StateMachine,
    mvn.`com.typesafe.akka:akka-actor_2.11:jar:2.4.11`,
    Toolbox6Modules.Logging,
    mvn.`com.typesafe.akka:akka-stream_2.11:2.4.11`,
    mvn.`me.chrons:boopickle_2.11:jar:1.2.4`

  )

  object Stream extends ScalaModule(
    "stream",
    mvn.`com.typesafe.akka:akka-stream_2.11:2.4.11`
  )

}
