package toolbox8.jartree.streamapp

import java.io.{File, FileInputStream, ObjectInputStream}
import java.net.ServerSocket

import com.typesafe.scalalogging.StrictLogging
import toolbox8.jartree.common.JarKey
import toolbox8.jartree.logging.LoggingSetup

import scala.util.control.NonFatal

/**
  * Created by maprohu on 21-11-2016.
  */
object StreamAppMain extends StrictLogging {

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

    val ctx = new RootContext(
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


    val port = if (args.length >= 2) {
      args(1).toInt
    } else {
      DefaultPort
    }

    val socket = new ServerSocket(port)

    logger.info(s"bound to port: ${port}")

    var id = 0

    while (true) {
      val client = socket.accept()

      new StreamAppThread(
        client,
        id,
        cache,
        rootDir,
        ctx
      ).start()

      id += 1
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

class RootContext(
  var root: Plugged
)