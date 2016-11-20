package toolbox8.jartree.testing

import akka.actor.{Address, RootActorPath}
import akka.pattern._
import akka.util.Timeout
import toolbox8.akka.actor.ActorSystemTools
import toolbox8.jartree.akka.PluggableServiceActor.{Clear, Query}
import toolbox8.jartree.akka._
import toolbox8.jartree.app.JarTreeMain
import toolbox8.rpi.installer.Rpis

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn

/**
  * Created by maprohu on 05-11-2016.
  */
object RunAkkaQuery {

  val Target = Rpis.MobileCable
//  val Target = Rpis.Home
//  val Target = Rpis.Localhost



  def main(args: Array[String]): Unit = {
    println {
      AkkaJartreeClientTools.run(Target) { i => import i._
        service ? Query
      }
    }
  }


}
