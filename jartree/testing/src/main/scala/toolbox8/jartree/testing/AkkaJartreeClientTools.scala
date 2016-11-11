package toolbox8.jartree.testing

import akka.actor.{ActorPath, ActorRef, ActorSystem, Address, RootActorPath}
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import toolbox8.akka.actor.ActorSystemTools
import toolbox8.jartree.akka.{JarTreeAkkaApi, PluggableServiceActor}
import toolbox8.jartree.akka.PluggableServiceActor.Clear
import toolbox8.jartree.app.JarTreeMain
import toolbox8.rpi.installer.{RpiInstaller, Rpis}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import akka.pattern._

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
    implicit val actorSystem = ActorSystemTools.actorSystem(
      "csufomen",
//      "192.168.10.122"
      "172.24.1.94"
    )
    import actorSystem.dispatcher

    val remoteActorSystem =
      RootActorPath(
        Address(
          protocol = "akka.tcp",
          system = target.actorSystemName,
          host = target.host,
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

}
