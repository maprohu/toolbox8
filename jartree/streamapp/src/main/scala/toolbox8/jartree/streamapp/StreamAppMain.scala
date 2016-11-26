package toolbox8.jartree.streamapp

import java.io.{File, FileInputStream, ObjectInputStream}
import java.net.{InetAddress, ServerSocket, SocketException}

import com.typesafe.scalalogging.StrictLogging
import monix.execution.atomic.Atomic
import monix.execution.cancelables.CompositeCancelable
import toolbox6.logging.LogTools
import toolbox8.jartree.common.JarKey
import toolbox8.jartree.logging.LoggingSetup

import scala.util.control.NonFatal

/**
  * Created by maprohu on 21-11-2016.
  */
object StreamAppMain extends StrictLogging with LogTools {

  val DefaultBindAddress = "127.0.0.1"
  val DefaultPort = 9981


  def main(args: Array[String]): Unit = {
    LoggingSetup.initLogging(args)

    val name = if (args.length >= 1) {
      args(0)
    } else {
      "jartree"
    }

    val dataDir = new File(s"/opt/${name}/data")
    val cacheDir = new File(dataDir, "cache")
    val rootDir = new File(dataDir, "root")
    rootDir.mkdirs()

    val rootConfigFile = new File(rootDir, ClassLoaderConfig.ClassLoaderConfigFileName)

    val cache = new JarCache(cacheDir)

    val ctx = new RootContext[Plugged](
      if (rootConfigFile.exists()) {
        logger.info("trying to load root from config file")
        try {
          val is = new ObjectInputStream(new FileInputStream(rootConfigFile))
          val r = try {
            is.readObject().asInstanceOf[ClassLoaderConfig[Root]]
          } finally {
            is.close()
          }

          val ri = cache.loadInstance(r, StreamAppMain.getClass.getClassLoader)

          ri.plug(
            PlugParams(
              previous = null,
              cache,
              rootDir
            )
          )
        } catch {
          case ex : Throwable =>
            logger.error("error during initial plugging", ex)
            DummyPlugged
        }
      } else {
        logger.info("no root config found, starting with dummy")
        DummyPlugged
      }
    )




    val bindAddress = if (args.length >= 2) {
      args(1)
    } else {
      DefaultBindAddress
    }

    val port = if (args.length >= 3) {
      args(2).toInt
    } else {
      DefaultPort
    }

    val socket = new ServerSocket(
      port,
      50,
      InetAddress.getByName(bindAddress)
    )

    logger.info(s"bound to port: ${bindAddress}:${port}")


    @volatile var stopped = false

    var id = 0

    val clientThreads = Atomic(Seq.empty[StreamAppThread[_]])

    logger.info("adding shutdown hook")
    Runtime.getRuntime.addShutdownHook(
      new Thread() {
        override def run(): Unit = {
          logger.info("starting shutdown")
          stopped = true
          logger.info("closing server socket")
          socket.close()
          val cth = clientThreads.get
          if (!cth.isEmpty) {
            logger.info("waiting for client threads")
            cth.foreach({ th =>
              logger.info(s"waiting for ${th}")
              th.join(5000)
            })
          }
          logger.info("unplugging")
          quietly { ctx.root.preUnplug }
          quietly { ctx.root.postUnplug }
          logger.info("shutdown sequence complete")
        }
      }
    )

    logger.info("starting accepting clients")
    while (!stopped) {
      try {
        val client = socket.accept()

        if (!stopped) {
          val thread = new StreamAppThread[Plugged](
            client,
            id,
            cache,
            rootDir,
            ctx,
            { th =>
              if (!stopped) {
                clientThreads.transform(ct => ct.filterNot(_ == th))
              }
            }
          )
          thread.start()
          clientThreads.transform(_ :+ thread)

          id += 1
        }
      } catch {
        case ex : SocketException =>
          if (stopped) {
            logger.info("server socket stopped: {}", ex.getMessage)
          } else {
            throw ex
          }
      }
    }


  }



}

case class PlugParams(
  previous : Any,
  cache: JarCache,
  dir: File
)

trait Root {
  def plug(params: PlugParams): Plugged
}

trait Plugged {
  def preUnplug : Any
  def postUnplug : Unit
}

trait Requestable[-Ctx <: Plugged, -In, +Out] {
  def request(ctx: Ctx, data: In) : Out
}


object DummyRoot extends Root {
  override def plug(params: PlugParams) = DummyPlugged
}

object DummyPlugged extends Plugged{
  override def postUnplug: Unit = ()
  override def preUnplug: Any = ()
}

object ClassLoaderConfig {
  val ClassLoaderConfigFileName = "classloader.config"
}
case class ClassLoaderConfig[T](
  jars: Vector[JarKey],
  className: String
)

class RootContext[P <: Plugged](
  var root: P
)