package toolbox8.android.packaging

import java.io.File

import com.android.builder.core.BuildToolsServiceLoader
import com.android.repository.Revision
import com.android.sdklib.BuildToolInfo
import com.android.utils.StdLogger
import com.android.utils.StdLogger.Level

/**
  * Created by maprohu on 18-12-2016.
  */
object Androidify {

  val SdkRoot = "/media/data/android-sdk"
  val AndroidRevision = new Revision(25, 0, 2)

  val logger = new StdLogger(Level.VERBOSE)

  val buildToolInfo =
    BuildToolInfo.fromStandardDirectoryLayout(
      AndroidRevision,
      new File(s"${SdkRoot}/build-tools/${AndroidRevision.toShortString}")
    )
  val buildToolServiceLoader = BuildToolsServiceLoader.INSTANCE.forVersion(buildToolInfo)

}
