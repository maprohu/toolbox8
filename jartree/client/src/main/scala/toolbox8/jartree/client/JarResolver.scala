package toolbox8.jartree.client

import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString
import mvnmod.builder.MavenCoordinatesImpl
import toolbox6.jartree.packaging.JarTreePackaging
import toolbox8.jartree.akka.JarCacheActor.JarKey

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

}
