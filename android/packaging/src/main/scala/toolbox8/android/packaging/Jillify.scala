package toolbox8.android.packaging

import java.io._
import java.nio.channels.{Channels, FileChannel}
import java.nio.file.{NoSuchFileException, StandardOpenOption}

import com.android.builder.core.BuildToolsServiceLoader
import com.android.jill.api.v01.Api01Config
import com.android.repository.Revision
import com.android.sdklib.BuildToolInfo
import com.android.utils.StdLogger
import com.android.utils.StdLogger.Level
import mvnmod.builder.{Module, ModuleVersion}
import toolbox8.jartree.client.JarResolver
import toolbox8.jartree.common.JarKey

/**
  * Created by maprohu on 18-12-2016.
  */
object Jillify {
  import Androidify._

  val jillService =
    buildToolServiceLoader
      .getSingleService(logger, BuildToolsServiceLoader.JILL)
      .get()

  def jill(
    lib: File,
    out: File
  ) = {
    val cfg =
      jillService.createConfig(classOf[Api01Config])

    cfg
      .setInputJavaBinaryFile(
        lib
      )

    cfg
      .setOutputJackFile(
        out
      )

    cfg
      .getTask
      .run()
  }

  val JilledFileName = "library.jack"
  val LockFileName = "lock.txt"
  val HashFileName = "hash.txt"

  def locked[T](what: => T)(implicit
    config: JillifyConfig = JillifyConfig()
  ) = {
    import ammonite.ops._
    val cache = Path(config.cache.getAbsoluteFile.getCanonicalPath)
    mkdir(cache)

    val lockFile = cache / LockFileName
    val ch = FileChannel.open(
      lockFile.toNIO,
      StandardOpenOption.CREATE,
      StandardOpenOption.READ,
      StandardOpenOption.WRITE
    )
    val l = ch.lock()
    try {
      what
    } finally {
      l.release()
      ch.close()
    }

  }

  def multi(
    modules: Seq[ModuleVersion]
  )(implicit
    config: JillifyConfig = JillifyConfig()
  ) = {
    modules.map(single)
  }

  def single(
    moduleVersion: ModuleVersion
  )(implicit
    config: JillifyConfig = JillifyConfig()
  ) = locked {
    import ammonite.ops._

    val cache = Path(config.cache.getAbsoluteFile.getCanonicalPath)


    val path : RelPath = empty / moduleVersion.groupId / moduleVersion.artifactId / moduleVersion.version

    val dir = cache / path

    mkdir(dir)

    val jarKey =
      JarKey(
        moduleVersion.groupId,
        moduleVersion.artifactId,
        moduleVersion.version
      )

    val hashed =
      JarResolver.resolveHash(jarKey)


    val jilled = (dir / JilledFileName)

    def doJill() = {
      println(s"jillifying: ${hashed}")
      jill(
        JarResolver.resolveFile(jarKey),
        jilled.toIO
      )
    }

    hashed
      .hash
      .map({ hash =>
        val hashFile = dir / HashFileName

        def doHashJill() = {
          doJill()
          write.over(
            hashFile,
            hash
          )
        }

        if (hashFile.toIO.exists()) {
          val currentHash = read(hashFile)
          if (hash != currentHash) {
            doHashJill()
          } else {
            println(s"already jillified: ${hashed}")
          }
        } else {
          doHashJill()
        }
      })
      .getOrElse({
        if (!jilled.toIO.exists()) {
          doJill()
        } else {
          println(s"already jillified: ${hashed}")
        }
      })


    jilled

  }





}

case class JillifyConfig(
  cache: File = new File("/media/data/cache/jillify")
)
