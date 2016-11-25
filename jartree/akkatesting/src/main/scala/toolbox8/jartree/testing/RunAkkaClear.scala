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
    println {
      AkkaJartreeClientTools.run(Target) { i => import i._
        service ? Clear
      }
    }
  }


}
