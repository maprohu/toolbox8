package toolbox8.jartree.testing

import akka.actor.{Address, Props, RootActorPath}
import akka.pattern._
import akka.util.Timeout
import toolbox8.akka.actor.{ActorSystemTools, ActorTools}
import toolbox8.jartree.akka.PluggableServiceActor.{Clear, PlugRequest}
import toolbox8.jartree.akka._
import toolbox8.jartree.app.JarTreeMain
import toolbox8.rpi.installer.Rpis

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by maprohu on 05-11-2016.
  */
object RunAkkaClear {

  val Target = Rpis.Home



  def main(args: Array[String]): Unit = {
    JarTreeMain.configureLogging("jartree", true)

    import toolbox8.akka.actor.ActorImplicits._
    implicit val actorSystem = ActorSystemTools.actorSystem("csufomen", "192.168.10.122")
    import actorSystem.dispatcher


    val remoteActorSystem =
      RootActorPath(
        Address(
          protocol = "akka.tcp",
          system = Target.actorSystemName,
          host = Target.host,
          port = Target.akkaPort
        )
      ) / "user"

    val fut = for {
      service <-
        actorSystem
          .actorSelection(
            remoteActorSystem / JarTreeAkkaApi.PluggableServiceActorName
          )
          .resolveOne()
      done <- service.ask(
        Clear
      )(Timeout(1.minute))
    } yield {
      done
    }




    println(
      Await.result(
        fut,
        Duration.Inf
      )
    )




  }


}
