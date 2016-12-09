package toolbox8.jartree.streamapp

import java.io._
import java.net.{InetAddress, ServerSocket, SocketException}

import com.typesafe.scalalogging.StrictLogging
import monix.execution.atomic.Atomic
import toolbox6.logging.LogTools
import toolbox8.jartree.common.JarKey
import toolbox8.jartree.logging.LoggingSetup
import toolbox8.jartree.requestapi.RequestMarker


/**
  * Created by maprohu on 21-11-2016.
  */
object StreamAppMain extends StrictLogging with LogTools {

  val DefaultBindAddress = "127.0.0.1"
  val DefaultPort = 33002

  def rootConfigFileFor(rootDir: File) =
    new File(rootDir, ClassLoaderConfig.ClassLoaderConfigFileName)

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

    val rootConfigFile = rootConfigFileFor(rootDir)

    val cache = new JarCache(cacheDir)

    val ctx = new RootContext(
      if (rootConfigFile.exists()) {
        logger.info("trying to load root from config file")
        try {
          val is = new ObjectInputStream(new FileInputStream(rootConfigFile))

          val r = try {
            is
              .readObject()
              .asInstanceOf[ClassLoaderConfig[Root]]
          } finally {
            is.close()
          }

          val (ri, cl) = cache.loadInstanceWithClassLoader(
            r,
            StreamAppMain.getClass.getClassLoader
          )

          val pl =
            ri
              .plug(
                PlugParams(
                  cache,
                  dataDir
                )
              )

          PluggedConfig(
            pl,
            cl,
            r
          )

        } catch {
          case ex : Throwable =>
            logger.error("error during initial plugging", ex)
            DummyPlugged.Config
        }
      } else {
        logger.info("no root config found, starting with dummy")
        DummyPlugged.Config
      },
      rootDir,
      cache,
      dataDir,
      StreamAppMain.getClass.getClassLoader
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

    val clientThreads = Atomic(Seq.empty[StreamAppThread])

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
          quietly { ctx.holder.get.plugged.stop() }
          logger.info("shutdown sequence complete")
        }
      }
    )

    logger.info("starting accepting clients")
    while (!stopped) {
      try {
        val client = socket.accept()

        if (!stopped) {
          val thread = new StreamAppThread(
            client,
            id,
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


trait Root {
  def plug(params: PlugParams) : Plugged
}

case class PlugParams(
  cache: JarCache,
  dir: File
)


trait Plugged {
  def marked[In, Out](marker: RequestMarker[In, Out], in: In) : Out
  def stop() : Unit
}


trait Requestable {
  def request(
    ctx: RootContext,
    in: InputStream,
    out: OutputStream
  ) : Unit
}


object DummyRoot extends Root {
  override def plug(params: PlugParams) = DummyPlugged
}

object DummyPlugged extends Plugged{
//  override def postUnplug: Unit = ()
//  override def preUnplug: Any = ()
  override def marked[In, Out](marker: RequestMarker[In, Out], in: In): Out = ???
  override def stop(): Unit = ()

  val Config = PluggedConfig(
    this,
    DummyPlugged.getClass.getClassLoader,
    ClassLoaderConfig(
      Vector.empty,
      DummyPlugged.getClass.getName
    )
  )
}

object ClassLoaderConfig {
  val ClassLoaderConfigFileName = "classloader.config"
}
case class ClassLoaderConfig[T](
  jars: Vector[JarKey],
  className: String
)

case class PluggedConfig(
  plugged: Plugged,
  classLoader: ClassLoader,
  classLoaderConfig: ClassLoaderConfig[Root]
)
class RootContext(
  root: PluggedConfig,
  rootDir: File,
  val cache: JarCache,
  val dataDir: File,
  val parent: ClassLoader
) extends StrictLogging with LogTools {
  val holder = Atomic(root)

  def stop() : Unit = {
    holder.transform { r =>
      quietly { r.plugged.stop() }
      DummyPlugged.Config
    }
  }

  def persist() = {
    val clc =
      holder
        .get
        .classLoaderConfig

    val os =
      new ObjectOutputStream(
        new FileOutputStream(
          StreamAppMain.rootConfigFileFor(rootDir)
        )
      )

    try {
      os.writeObject(clc)
    } finally {
      os.close()
    }
  }
}