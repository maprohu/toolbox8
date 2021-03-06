package toolbox8.modules

import mvnmod.builder.{ScalaModule, SubModuleContainer}
import toolbox6.modules.Toolbox6Modules

/**
  * Created by martonpapp on 31/08/16.
  */
object Akka8Modules {

  implicit val Container = SubModuleContainer(Toolbox8Modules.Root, "akka")

//  object Actor extends ScalaModule(
//    "actor",
//    mvn.`com.typesafe.akka:akka-actor_2.11:jar:2.4.12`,
//    mvn.`com.typesafe.scala-logging:scala-logging_2.11:jar:3.4.0`,
//    mvn.`me.chrons:boopickle_2.11:jar:1.2.4`,
//    mvn.`io.monix:monix-execution_2.11:jar:2.0.6`
//  )
//
//  object StateMachine extends ScalaModule(
//    "statemachine",
//    Toolbox6Modules.StateMachine,
//    Toolbox6Modules.Logging,
//    mvn.`com.typesafe.akka:akka-actor_2.11:jar:2.4.12`,
//    mvn.`com.typesafe.akka:akka-stream_2.11:jar:2.4.12`,
//    mvn.`me.chrons:boopickle_2.11:jar:1.2.4`
//
//  )
//
//  object Stream extends ScalaModule(
//    "stream",
//    Toolbox6Modules.Common.R3,
//    Toolbox6Modules.Logging.R2,
//    mvn.`com.typesafe.akka:akka-stream_2.11:jar:2.4.12`,
//    mvn.`com.typesafe.akka:akka-slf4j_2.11:jar:2.4.12`,
//    mvn.`io.monix:monix-execution_2.11:jar:2.0.6`
//  )



}
