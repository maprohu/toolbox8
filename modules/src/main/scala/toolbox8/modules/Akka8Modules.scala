package toolbox8.modules

import maven.modules.builder.{ScalaModule, SubModuleContainer}
import toolbox6.modules.Toolbox6Modules

/**
  * Created by martonpapp on 31/08/16.
  */
object Akka8Modules {

  implicit val Container = SubModuleContainer(Toolbox8Modules.Root, "akka")

  object Actor extends ScalaModule(
    "actor",
    mvn.`com.typesafe.akka:akka-actor_2.11:jar:2.4.12`
  )

  object StateMachine extends ScalaModule(
    "statemachine",
    Toolbox6Modules.StateMachine,
    mvn.`com.typesafe.akka:akka-actor_2.11:jar:2.4.12`,
    Toolbox6Modules.Logging.R1,
    mvn.`com.typesafe.akka:akka-stream_2.11:2.4.12`,
    mvn.`me.chrons:boopickle_2.11:jar:1.2.4`

  )

  object Stream extends ScalaModule(
    "stream",
    mvn.`com.typesafe.akka:akka-stream_2.11:2.4.12`,
    Toolbox6Modules.Logging.R1,
    mvn.`com.typesafe.akka:akka-slf4j_2.11:2.4.12`,
    mvn.`io.monix:monix-execution_2.11:jar:2.0.5`,
    Toolbox6Modules.Common.R1


  )

}
