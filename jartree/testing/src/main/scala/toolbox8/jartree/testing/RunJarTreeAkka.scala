package toolbox8.jartree.testing

import toolbox8.jartree.app.{JarTreeAkkaMain, JarTreeMain}
import toolbox8.jartree.standaloneapi.JarTreeAkkaApi
import akka.pattern._
import toolbox8.jartree.akka.JarTreeActor.Query
import toolbox8.jartree.akka.JarTreeAkka

/**
  * Created by maprohu on 05-11-2016.
  */
object RunJarTreeAkka {


  def main(args: Array[String]): Unit = {
    JarTreeMain.configureLogging("jartree", true)

    implicit val actorSystem = JarTreeAkka
      .run(
        "jartree",
        "192.168.10.122"
      )
    import actorSystem.dispatcher
    import toolbox8.akka.actor.ActorImplicits._

    val path =
      actorSystem / JarTreeAkkaApi.JarTreeActorName

    println(path)

    for {
      ref <-
        actorSystem
          .actorSelection(
            path
          )
          .resolveOne()
      q <-
        ref ? Query


    } {
      println(q)
    }



  }


}
