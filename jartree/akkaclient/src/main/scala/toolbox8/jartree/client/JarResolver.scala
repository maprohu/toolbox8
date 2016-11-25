package toolbox8.jartree.client

import java.io.FileInputStream
import java.util.jar.JarFile

import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString
import monix.execution.atomic.Atomic
import mvnmod.builder.{HasMavenCoordinates, MavenCoordinatesImpl}
import org.apache.commons.codec.binary.Base64
import org.jboss.shrinkwrap.resolver.api.maven.Maven
import toolbox6.jartree.impl.JarCache
import toolbox6.jartree.packaging.JarTreePackaging
import toolbox8.jartree.common.JarKey


/**
  * Created by maprohu on 06-11-2016.
  */
object JarResolver {

  val resources : JarKey => Source[ByteString, _] = { key =>
    FileIO
      .fromPath(
        JarTreePackaging
          .resolveFile(
            MavenCoordinatesImpl(
              groupId = key.groupId,
              artifactId = key.artifactId,
              version = key.version
            )
          )
          .toPath
      )
  }

  def resolveHash(jarKey: JarKey) = {
    jarKey.copy(
      hash = getHash(jarKey)
    )
  }

  private val ManagedIdMap = Atomic(Map[JarKey, Option[String]]())

  def clear() = {
    ManagedIdMap.set(Map())
  }

  def getHash(maven: JarKey) : Option[String] = {
    ManagedIdMap.transformAndExtract({ map =>
      map
        .get(maven)
        .map({ id => (id, map)})
        .getOrElse({
          val id = {
            if (maven.isSnapshot) {
              val file = resolveFile(maven)

              val jf = new JarFile(file)
              val entry = jf.getJarEntry("META-INF/build.timestamp")

              val hash =
                Option(entry)
                  .map({ e =>

                    scala.io.Source.fromInputStream(
                      jf.getInputStream(e),
                      "UTF-8"
                    ).mkString

                  })
                  .getOrElse(
                    Base64.encodeBase64String(
                      JarCache.calculateHash(
                        new FileInputStream(file)
                      )
                    )
                  )

              jf.close()

              Some(hash)
            } else {
              None
            }
          }

          (id, map.updated(maven, id))
        })
    })

  }

  def resolveFile(maven: JarKey) = {
    Maven
      .resolver()
      .resolve(maven.toCanonical)
      .withoutTransitivity()
      .asSingleFile()
  }


}
