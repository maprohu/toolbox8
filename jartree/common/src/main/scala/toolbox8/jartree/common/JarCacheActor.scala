package toolbox8.jartree.common


/**
  * Created by maprohu on 05-11-2016.
  */
case class JarKey(
  groupId: String,
  artifactId: String,
  version: String,
  classifier: Option[String] = None,
  hash: Option[String] = None
) {
  def isSnapshot = version.endsWith("SNAPSHOT")
  def toCanonical = {
    s"${groupId}:${artifactId}:jar:${version}"
  }
}

