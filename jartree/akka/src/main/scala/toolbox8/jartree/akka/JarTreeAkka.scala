package toolbox8.jartree.akka

import java.io.File
import java.nio.file.{Path, Paths}

import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.scalalogging.LazyLogging
import toolbox6.logging.LogTools
import toolbox8.akka.actor.{ActorSystemTools, ActorTools}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by maprohu on 05-11-2016.
  */
object JarTreeAkka extends LazyLogging with LogTools {

  def run(
    name: String,
    address: String,
    port: Int,
    basePathOpt : Option[Path] = None
  ) = {
    val basePath =
      basePathOpt
        .getOrElse(
          Paths.get(s"/opt/${name}")
        )

    val cacheDir = basePath.resolve("jarcache")
    val persistenceDir = basePath.resolve("persistence")

    implicit val actorSystem = ActorSystemTools.actorSystem(
      name = name,
      address = address,
      port = port,
      persistence = Some(
        persistenceDir
      )
    )


    val cacheActor =
      actorSystem
        .actorOf(
          Props(
            classOf[JarCacheActor],
            JarCacheActor.Config(
              dir = cacheDir
            )
          )
        )

    val service =
      actorSystem
        .actorOf(
          Props(
            classOf[PluggableServiceActor],
            PluggableServiceActor.Config(
              cache = cacheActor,
              parent = JarTreeAkka.getClass.getClassLoader
            )
          )
        )



    logger.info(s"started: ${name}")

    Output(
      actorSystem = actorSystem,
      jarCache = cacheActor,
      pluggableService = service
    )
  }

  case class Output(
    actorSystem: ActorSystem,
    jarCache: ActorRef,
    pluggableService: ActorRef
  )

}
