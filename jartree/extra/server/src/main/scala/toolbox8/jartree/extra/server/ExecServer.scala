package toolbox8.jartree.extra.server

import java.nio.file.Path

import akka.event.Logging
import akka.stream.Attributes
import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import toolbox6.jartree.api._
import toolbox6.jartree.impl.JarTree
import toolbox6.jartree.util.JarTreeTools
import toolbox8.akka.statemachine.AkkaStreamCoding
import toolbox8.jartree.extra.shared.ExecProtocol.Executable
import toolbox8.jartree.extra.shared.{HasLogFile, HasStorageDir}
import toolbox8.jartree.standaloneapi.{JarTreeStandaloneContext, PeerInfo, Service}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by maprohu on 01-11-2016.
  */
object ExecServer extends LazyLogging {

  def flow[Ctx](
    ctx: Ctx,
    instanceResolver: ClassLoaderResolver
  )(implicit
    executionContext: ExecutionContext
  ) = {
    Flow[ByteString]
      .prefixAndTail(1)
      .flatMapConcat({
        case (heads, tail) =>
          val head = heads.head
          import toolbox6.pickling.PicklingTools._
          val request =
            Unpickle[ClassRequest[Executable[Ctx]]]
              .fromBytes(head.asByteBuffer)

          logger.info(s"executing: ${request}")

          Source
            .fromFuture(
              JarTreeTools
                .resolve(
                  instanceResolver,
                  request
                )
            )
            .log("exec-flow").withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
            .flatMapConcat({ e =>
              logger.info(s"starting flow: ${e}")
              tail
                .via(e.flow(ctx))
            })
      })
  }

}

class ExecService(
  classLoader: ClassLoader,
  jarTreeContext: JarTreeContext
)(implicit
  executionContext: ExecutionContext
) extends Service with HasLogFile with HasStorageDir{

  override val logFile: Option[Path] = jarTreeContext.log.map(_.toPath)
  override val storageDir: Option[Path] = jarTreeContext.storage.map(_.toPath)

  val jarTree = new JarTree(
    classLoader,
    jarTreeContext.cache
  )
  override def apply(info: PeerInfo): Future[Flow[ByteString, ByteString, _]] = {
    Future.successful(
      ExecServer.flow(
        this,
        jarTree
      )
    )
  }

  override def close(): Unit = {
    // TODO wait for flows to stop
    jarTree.clear()
  }
}

class ExecPlugger extends JarPlugger[Service, JarTreeStandaloneContext] {
  override def pull(
    params: PullParams[Service, JarTreeStandaloneContext]
  ): Future[JarPlugResponse[Service]] = {
    import params._
    import context._
    import actorSystem.dispatcher
    Future.successful(
      JarPlugResponse[Service](
        new ExecService(
          classLoader,
          context.jarTreeContext
        ),
        () => previous.close()
      )
    )
  }
}
