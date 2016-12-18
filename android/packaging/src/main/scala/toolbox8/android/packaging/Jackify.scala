package toolbox8.android.packaging

import com.android.builder.core.BuildToolsServiceLoader
import mvnmod.builder.{Module, ModulePath}
import toolbox8.modules.AndroidModules

/**
  * Created by maprohu on 18-12-2016.
  */
object Jackify {
  import Androidify._

  val jackService =
    buildToolServiceLoader
      .getSingleService(logger, BuildToolsServiceLoader.JACK)
      .get()

  def jack(
    module: Module
  ) = {

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

    Jillify
      .multi(mods)

  }


}
