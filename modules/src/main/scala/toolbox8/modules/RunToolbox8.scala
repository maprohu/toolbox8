package toolbox8.modules

import java.io.File

import maven.modules.builder.Module.ConfiguredModule
import maven.modules.builder.{Module, PlacedRoot}
import scala.collection.immutable._

/**
  * Created by pappmar on 29/08/2016.
  */
object RunToolbox8 {

  val RootDir = new File("../toolbox8")

  val Roots = Seq[PlacedRoot](
    Toolbox8Modules.Root -> RootDir
  )

  val Modules = Seq[ConfiguredModule](
    JarTree8Modules.Standalone,
    JarTree8Modules.Installer,
    JarTree8Modules.Packaging,
    JarTree8Modules.Testing
//    Toolbox8Modules.JarTree,
//    Servlet31Modules.SingleApi,
//    Servlet31Modules.RunApi,
//    Servlet31Modules.SampleRunner,
//    Servlet31Modules.Webapp,
//    Akka8Modules.Stream,
//    Common8Modules.Tools
  )

  def main(args: Array[String]): Unit = {

    Module.generate(
      Roots,
      Modules
    )

  }

}
