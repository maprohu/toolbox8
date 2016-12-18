package toolbox8.android.packaging

import java.io.File

import com.android.builder.core.BuildToolsServiceLoader
import com.android.jack.api.v03.Api03Config
import mvnmod.builder.{Module, ModulePath}
import toolbox8.modules.AndroidModules

/**
  * Created by maprohu on 18-12-2016.
  */
object Jackify {
  import Androidify._
  import scala.collection.JavaConversions._


  val jackService =
    buildToolServiceLoader
      .getSingleService(logger, BuildToolsServiceLoader.JACK)
      .get()

  def jack(
    module: Module,
    jackedFile: File
  ) = {
    jackedFile.getParentFile.mkdirs()

    val mods =
      module
        .forTarget(
          ModulePath(
            AndroidModules.Runtime,
            None
          )
        )
        .resolve
        .classPath

    val jilled =
      Jillify
        .multi(mods)

    val cfg =
      jackService.createConfig(classOf[Api03Config])

    cfg
      .setImportedJackLibraryFiles(
        jilled.map(_.toIO)
      )

    cfg
      .setOutputJackFile(
        jackedFile
      )

    cfg
      .getTask
      .run()

  }


}
