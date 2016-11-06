package toolbox8.jartree.app

//import akka.actor.ActorSystem
//import com.typesafe.config.ConfigFactory
//import com.typesafe.scalalogging.LazyLogging
//import toolbox6.logging.LogTools
//import toolbox8.jartree.akka.JarTreeAkka
//import toolbox8.jartree.standaloneapi.Protocol
//
//import scala.concurrent.Await
//import scala.concurrent.duration._
//
///**
//  * Created by maprohu on 05-11-2016.
//  */
//object JarTreeAkkaMain extends LazyLogging with LogTools {
//
//  def main(args: Array[String]): Unit = {
//    val (log, name) = JarTreeMain.parseName(args)
//
//    val address = if (args.length >= 2) {
//      args(1)
//    } else {
//      "localhost"
//    }
//
//    JarTreeAkka.run(name, address)
//
//  }
//
//
//
//}
