package toolbox8.jartree.testing

import java.io.File

import akka.actor.{ActorSystem, Props}
import toolbox8.akka.actor.{ActorSystemTools, ActorTools}
import toolbox8.jartree.akka.{JarCacheActor, JarCacheUploaderActor, ParentLastUrlClassloader, PluggableServiceActor}
import toolbox8.jartree.app.JarTreeMain
import toolbox8.jartree.client.JarResolver
import toolbox8.modules.{Akka8Modules, JarTree8Modules, Toolbox8Modules}
import akka.pattern._
import mvnmod.builder.ModulePath
import toolbox8.jartree.akka.PluggableServiceActor.PlugRequest
import toolbox8.jartree.common.JarKey

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by maprohu on 05-11-2016.
  */
object RunJarTreeAkka {


  def main(args: Array[String]): Unit = {
    JarTreeMain.configureLogging("jartree", true)

    import toolbox8.akka.actor.ActorImplicits._
    import ActorSystemTools.Implicit._
    import actorSystem.dispatcher

    val cacheDir = new File("../voice/target/jarcache")

    val cacheActor =
      actorSystem
        .actorOf(
          Props(
            classOf[JarCacheActor],
            JarCacheActor.Config(
              dir = cacheDir.toPath
            )
          )
        )

    val jars =
      JarTree8Modules
        .Echo
        .forTarget(
          ModulePath(
            JarTree8Modules.Akka,
            None
          )
        )
        .classPath
        .map({ m =>
          JarKey(
            groupId = m.groupId,
            artifactId = m.artifactId,
            version = m.version
          )
        })

    val uploader =
      actorSystem
        .actorOf(
          Props(
            classOf[JarCacheUploaderActor],
            JarCacheUploaderActor.Config(
              cache = cacheActor,
              keys = jars,
              resources = JarResolver.resources
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
              parent = RunJarTreeAkka.getClass.getClassLoader
            )
          )
        )

    println(
      Await.result(
        for {
          _ <- ActorTools.watchFuture(uploader)
          _ <- service ? PlugRequest(
            classLoader = Some(jars),
            className = "toolbox8.jartree.echo.TestPluggable"
          )
        } yield {
          "plugged"
        },
        Duration.Inf
      )
    )




  }


}
