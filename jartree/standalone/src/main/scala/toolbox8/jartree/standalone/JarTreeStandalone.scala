package toolbox8.jartree.standalone

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Tcp}
import akka.util.ByteString
import toolbox6.jartree.impl.JarTreeBootstrap
import toolbox8.jartree.standaloneapi.JarTreeStandaloneContext

/**
  * Created by martonpapp on 15/10/16.
  */
object JarTreeStandalone {

  def run[Processor](
    port: Int
  ) = {
//    JarTreeBootstrap
//      .init[Processor, JarTreeStandaloneContext](
//
//
//      )


    val flow =
      Flow[ByteString]


    implicit val actorSystem = ActorSystem()
    implicit val materializer = ActorMaterializer()

    Tcp()
      .bindAndHandle(
        flow,
        "0.0.0.0",
        port
      )


  }




}
