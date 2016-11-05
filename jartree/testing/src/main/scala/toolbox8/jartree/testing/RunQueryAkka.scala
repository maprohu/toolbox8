package toolbox8.jartree.testing

import akka.actor.{ActorSystem, Identify}
import toolbox8.rpi.installer.Rpis
import akka.pattern._
import toolbox8.akka.actor.ActorSystemTools
import toolbox8.jartree.app.JarTreeMain

/**
  * Created by maprohu on 05-11-2016.
  */
object RunQueryAkka {

  val Target = Rpis.Home

  def main(args: Array[String]): Unit = {
    JarTreeMain.configureLogging("csuf", true)

    implicit val actorSystem = ActorSystemTools.actorSystem(
      "csuf",
      "192.168.10.122",
      5555
    )

    val ref =
      actorSystem
        .actorSelection(s"akka.tcp://voicer@${Target.host}:${Target.akkaPort}")

    import actorSystem.dispatcher
    import toolbox8.akka.actor.ActorImplicits._

//    ref
//      .?(Identify)
//      .foreach(println)

    ref
      .resolveOne()
      .foreach(println)





  }

}
