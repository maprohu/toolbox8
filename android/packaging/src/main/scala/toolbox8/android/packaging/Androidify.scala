package toolbox8.android.packaging

import java.io.File

import com.android.builder.core.BuildToolsServiceLoader
import com.android.repository.Revision
import com.android.sdklib.BuildToolInfo
import com.android.utils.StdLogger
import com.android.utils.StdLogger.Level

import scala.util.Properties

/**
  * Created by maprohu on 18-12-2016.
  */
object Androidify {

//  val SdkRoot = "/media/data/android-sdk"
  val SdkRoot = Properties.envOrElse("ANDROID_HOME", "/media/data/android-sdk")
  val AndroidRevision = new Revision(25, 0, 2)
  val BuildToolsDir = new File(s"${SdkRoot}/build-tools/${AndroidRevision.toShortString}")
  val PlatformVersion = 23
  val PlatformDir = new File(s"${SdkRoot}/platforms/android-${PlatformVersion}")
  val AndroidJar = new File(PlatformDir, "android.jar")


  val logger = new StdLogger(Level.VERBOSE)

  val buildToolInfo =
    BuildToolInfo.fromStandardDirectoryLayout(
      AndroidRevision,
      BuildToolsDir
    )

  val buildToolServiceLoader = BuildToolsServiceLoader.INSTANCE.forVersion(buildToolInfo)

}
