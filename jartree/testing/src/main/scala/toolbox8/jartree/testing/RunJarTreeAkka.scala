package toolbox8.jartree.testing

import java.io.File

import akka.actor.{ActorSystem, Props}
import toolbox8.akka.actor.{ActorSystemTools, ActorTools}
import toolbox8.jartree.akka.JarCacheActor.JarKey
import toolbox8.jartree.akka.{JarCacheActor, JarCacheUploaderActor, ParentLastUrlClassloader}
import toolbox8.jartree.app.JarTreeMain
import toolbox8.jartree.client.JarResolver
import toolbox8.modules.{Akka8Modules, Toolbox8Modules}
import akka.pattern._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by maprohu on 05-11-2016.
  */
object RunJarTreeAkka {


  def main(args: Array[String]): Unit = {
    JarTreeMain.configureLogging("jartree", true)

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
      Toolbox8Modules
        .Dummy
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

    println(
      Await.result(
        for {
          _ <- ActorTools.watchFuture(uploader)
          _ = println("loading")
          cl <- ParentLastUrlClassloader(
            jars = jars,
            parent = RunJarTreeAkka.getClass.getClassLoader,
            cacheActor
          )
        } yield {
          println(
            cl.loadClass("toolbox8.dummy.DummyMain")
          )
        },
        Duration.Inf
      )
    )




  }


}
