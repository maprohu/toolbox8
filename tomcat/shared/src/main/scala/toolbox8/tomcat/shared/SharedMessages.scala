package toolbox8.tomcat.shared

/**
  * Created by pappmar on 14/03/2017.
  */
object SharedMessages {

}

case class JarCoords(
  groupId: String,
  artifactId: String,
  version: String,
  classifier: Option[String] = None,
  hash: Option[String] = None
)

sealed trait ClientToServer
sealed trait ServerToClient

case class VerifyJars(
  coords: Seq[JarCoords]
) extends ClientToServer

case class JarsVerified(
  missing: Seq[JarCoords]
) extends ServerToClient

case class UploadJarStart(
  coords: JarCoords
) extends ClientToServer

case class UploadJarPiece(
  data: Array[Byte]
) extends ClientToServer

case class UploadJarEnd(
  md5: Array[Byte]
) extends ClientToServer

case class JarUploaded(
  coords: JarCoords,
  success: Boolean
) extends ServerToClient

case class Run(
  jars: Seq[JarCoords],
  className: String
) extends ClientToServer

case class Wrapped(
  data: Array[Byte]
) extends ClientToServer with ServerToClient

