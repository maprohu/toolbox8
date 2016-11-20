package toolbox8.jartree.testing

import akka.actor.{ActorPath, ActorRef, ActorSystem, Address, Props, RootActorPath}
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import toolbox8.akka.actor.{ActorSystemTools, ActorTools}
import toolbox8.jartree.akka.{JarCacheUploaderActor, JarTreeAkkaApi, PluggableServiceActor}
import toolbox8.jartree.akka.PluggableServiceActor.{Clear, PlugRequest}
import toolbox8.jartree.app.JarTreeMain
import toolbox8.rpi.installer.{RpiInstaller, Rpis}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import akka.pattern._
import mvnmod.builder.{Module, ModulePath}
import toolbox8.jartree.akka.JarCacheActor.JarKey
import toolbox8.jartree.client.JarResolver
import toolbox8.modules.JarTree8Modules

/**
  * Created by maprohu on 10-11-2016.
  */
object AkkaJartreeClientTools {

  class Context(
    val service : ActorRef,
    val cache : ActorRef,
    val remoteActorSystem : ActorPath
  )(implicit
    val actorSystem : ActorSystem,
    val materializer : Materializer,
    val timeout : Timeout = 10.seconds
  ) extends PipeToSupport with GracefulStopSupport with AskSupport with FutureTimeoutSupport {
    implicit val executionContext : ExecutionContext = actorSystem.dispatcher
  }

  def run[T](target: RpiInstaller.Config)(fn: Context => Future[T]) : T = {
    JarTreeMain.configureLogging("jartree", true)

    import toolbox8.akka.actor.ActorImplicits._
    implicit val actorSystem =
      ActorSystemTools
        .actorSystem(
          name = "csufomen",
          port = Rpis.ClientPort
    //      "192.168.10.122"
    //      "172.24.1.94"
        )
    import actorSystem.dispatcher

    val remoteActorSystem =
      RootActorPath(
        Address(
          protocol = "akka.tcp",
          system = target.actorSystemName,
          host = "localhost",
          port = target.akkaPort
        )
      ) / "user"

    val cacheFuture =
      actorSystem
        .actorSelection(
          remoteActorSystem / JarTreeAkkaApi.JarCacheActorName
        )
        .resolveOne()

    val serviceFuture =
      actorSystem
        .actorSelection(
          remoteActorSystem / JarTreeAkkaApi.PluggableServiceActorName
        )
        .resolveOne()

    val fut = for {
      cache <- cacheFuture
      _ = println(s"cache: ${cache}")
      service <- serviceFuture
      _ = println(s"service: ${service}")
      done <- fn(
        new Context(
          service = service,
          cache = cache,
          remoteActorSystem = remoteActorSystem
        )(
          actorSystem = actorSystem,
          materializer = ActorMaterializer()
        )
      )
    } yield {
      done
    }

    val r = Await.ready(
      fut,
      Duration.Inf
    )

    Await.result(
      actorSystem.terminate(),
      Duration.Inf
    )

    r.value.get.get
  }


  def plug(
    pluggableModule: Module,
    pluggableClassName: String,
    rpiTarget: RpiInstaller.Config,
    runtimeTarget: ModulePath = ModulePath(JarTree8Modules.Akka, None)
  ) = {
    val jars =
      pluggableModule
        .forTarget(
          runtimeTarget
        )
        .classPath
        .map({ m =>
          JarResolver.resolveHash(
            JarKey(
              groupId = m.groupId,
              artifactId = m.artifactId,
              version = m.version
            )
          )
        })
    val r = AkkaJartreeClientTools.run(rpiTarget) { i => import i._
      for {
        _ <- {
          val uploader =
            actorSystem
              .actorOf(
                Props(
                  classOf[JarCacheUploaderActor],
                  JarCacheUploaderActor.Config(
                    cache = cache,
                    keys = jars,
                    resources = JarResolver.resources
                  )
                )
              )

          ActorTools
            .watchFuture(uploader)
        }
        done <- service.ask(
          PlugRequest(
            classLoader = Some(jars),
            className = pluggableClassName
          )
        )(Timeout(1.minute))
      } yield {
        done
      }
    }

    println(r)
  }
}
