package toolbox8.jartree.standalone

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import toolbox6.jartree.impl.JarTreeBootstrap
import toolbox8.jartree.standaloneapi.JarTreeStandaloneContext

/**
  * Created by martonpapp on 15/10/16.
  */
object JarTreeStandalone {

  def run[Processor](
    port: Int
  ) = {
    JarTreeBootstrap
      .init[Processor, JarTreeStandaloneContext](


      )



    import akka.http.scaladsl.server.Directives._
    val route = {
      complete("ok")
    }

    implicit val actorSystem = ActorSystem()
    implicit val materializer = ActorMaterializer()

    Http()
      .bindAndHandle(
        route,
        "0.0.0.0",
        port
      )


  }




}
