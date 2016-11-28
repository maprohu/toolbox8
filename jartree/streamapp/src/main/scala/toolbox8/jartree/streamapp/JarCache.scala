package toolbox8.jartree.streamapp

import java.io.File
import java.net.URLEncoder

import com.typesafe.scalalogging.StrictLogging
import toolbox6.jartree.common.ParentLastUrlClassloader
import toolbox8.jartree.common.JarKey

/**
  * Created by maprohu on 21-11-2016.
  */
class JarCache(
  dir: File
) extends StrictLogging {
  dir.mkdirs()
  logger.info(s"starting jar cache in ${dir}")

  def get(
    key: JarKey
  ) : File = {
    new File(
      dir,
      URLEncoder.encode(
        s"${key.groupId}:${key.artifactId}:${key.version}:${key.classifier.getOrElse("")}:${key.hash.getOrElse("")}.jar",
        "UTF-8"
      )
    )
  }

  def loadInstance[T](
    config: ClassLoaderConfig[T],
    parent: ClassLoader
  ) = {
    val cl =
      if (config.jars.isEmpty) {
        parent
      } else {
        new ParentLastUrlClassloader(
          config.jars.map(j => get(j).toURI.toURL),
          parent
        )
      }

    val clazz = cl.loadClass(config.className)

    clazz.newInstance().asInstanceOf[T]
  }

}

