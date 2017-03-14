package toolbox8.tomcat.jarservlet
import java.io.{ByteArrayOutputStream, File, FileInputStream, FileOutputStream}
import java.net.{URL, URLEncoder}
import java.nio.ByteBuffer
import java.nio.file.Paths
import javax.servlet.ServletContext
import javax.websocket.server.ServerEndpointConfig.Configurator
import javax.websocket.server.{ServerContainer, ServerEndpointConfig}

import akka.stream.scaladsl.Flow
import akka.util.ByteString
import com.typesafe.scalalogging.StrictLogging
import toolbox8.tomcat.jarservlet.JarServlet.Plugged
import toolbox8.tomcat.shared.JarCoords

import scala.util.control.NonFatal


case class PersistedConfig(
  version: Int,
  jars: Seq[String],
  className: String
)


case class EmbeddedJar(
  path: String,
  coords: JarCoords
)

case class EmbeddedJars(
  jars: Seq[EmbeddedJar],
  className: String
)

trait FlowProvider {
  def apply(global: Global, persisted: PersistedProvider) : Flow[ByteString, ByteString, _]
}

abstract class PersistedProvider extends JarServletProvider with StrictLogging { self =>

  def appName: String

  def basePath = s"/apps/${appName}"

  def configPath = s"${basePath}/config.dat"

  def shouldCleanStart(version: Int) : Boolean = false

  def jarsDir = s"${basePath}/jars"

  def logsDir = s"${basePath}/logs"

  def embeddedJarsPath = s"/${PersistedProvider.EmbeddedJarsPath}"

  def loadEmbeddedJars = {
    val ba = Array.ofDim[Byte](4096)

    val is =
      getClass
        .getClassLoader
        .getResourceAsStream(embeddedJarsPath)

    try {
      val bos = new ByteArrayOutputStream()

      Iterator
        .continually(is.read(ba))
        .takeWhile(_ >= 0)
        .foreach({ count =>
          bos.write(ba, 0, count)
        })

      import boopickle.Default._
      Unpickle[EmbeddedJars].fromBytes(ByteBuffer.wrap(bos.toByteArray))
    } finally {
      is.close()
    }
  }

  def cleanStart(servletContext: ServletContext, global: Global) : Plugged = {
    val jars = loadEmbeddedJars

    new File(jarsDir).mkdirs()

    val ba = Array.ofDim[Byte](4096)
    val urls =
      jars
        .jars
        .map({ jar =>
          val file = jarFile(jar.coords)
          val out = new FileOutputStream(file)

          try {
            val is =
              self
                .getClass
                .getClassLoader
                .getResourceAsStream(
                  jar.path
                )

            try {
              Iterator
                .continually(is.read(ba))
                .takeWhile(_ >= 0)
                .foreach({ count =>
                  out.write(ba, 0, count)
                })

            } finally {
              is.close()
            }

          } finally {
            out.close()
          }

          file.toURI.toURL
        })

    loadPlugged(
      servletContext,
      global,
      urls,
      jars.className
    )
  }

  def loadInstance[T](
    urls: Seq[URL],
    className: String,
    parent: ClassLoader = self.getClass.getClassLoader
  ) = {
    val classLoader = new ParentLastUrlClassloader(
      urls,
      parent
    )

    classLoader
      .loadClass(className)
      .newInstance()
      .asInstanceOf[T]
  }

  def loadPlugged(
    servletContext: ServletContext,
    global: Global,
    urls: Seq[URL],
    className: String
  ) = {
    val provider = loadInstance[JarServletProvider](
      urls,
      className
    )

    provider.apply(servletContext, global)
  }

  def loadPersistedConfig = {
    val is = new FileInputStream(configPath)
    val ch = is.getChannel
    val size = ch.size()
    val bb = ByteBuffer.allocate(size.toInt)
    ch.read(bb)
    bb.rewind()

    import boopickle.Default._

    Unpickle[PersistedConfig].fromBytes(bb)
  }

  def jarFile(jarCoords: JarCoords) = {
    jarPath(jarCoords).toFile
  }

  def jarPath(jarCoords: JarCoords) = {
    Paths.get(s"${jarsDir}/${PersistedProvider.fileName(jarCoords)}")
  }

  def jarFileExists(jarCoords: JarCoords) = {
    jarFile(jarCoords).exists()
  }

  LogbackConfigurator.reset()
  LogbackConfigurator.configure(appName, new File(logsDir))

  override def apply(servletContext: ServletContext, global: Global): Plugged = {

    val serverContainer =
      servletContext
        .getAttribute(classOf[ServerContainer].getName)
        .asInstanceOf[ServerContainer]

    val websocket = new JarServletWsEndpoint(self, global)

    serverContainer.addEndpoint(
      ServerEndpointConfig.Builder
        .create(
          classOf[JarServletWsEndpoint],
          "/private/ws"
        )
        .configurator(
          new Configurator {
            override def getEndpointInstance[T](endpointClass: Class[T]): T = websocket.asInstanceOf[T]
          }
        )
        .build()
    )

    try {
      val config = loadPersistedConfig

      if (shouldCleanStart(config.version)) {
        cleanStart(servletContext, global)
      } else {
        loadPlugged(
          servletContext,
          global,
          config
            .jars
            .map(p => new File(p).toURI.toURL),
          config.className
        )
      }
    } catch {
      case NonFatal(ex) =>
        logger.info(s"Starting clean: ${ex.getMessage}")
        cleanStart(servletContext, global)
    }

  }

}

object PersistedProvider {

  def fileName(coords: JarCoords) = {
    import coords._
    URLEncoder.encode(
      s"${groupId}:${artifactId}:${version}:${classifier.getOrElse("")}:${hash.getOrElse("")}.jar",
      "UTF-8"
    )
  }

  val EmbeddedJarsPath = "embedded-jars.dat"

}
