package toolbox8.modules

import java.io.File

import mvnmod.builder.Module.ConfiguredModule
import mvnmod.builder.{Module, PlacedRoot}

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
    Toolbox8Modules.Modules,
    Toolbox8Modules.Common,
    Toolbox8Modules.Leveldb,
    RpiModules.Remote,
    RpiModules.Installer,
    RpiModules.DBus,
    RpiModules.Bluetooth,
    Toolbox8Modules.Dummy,
    JarTree8Modules.Akka,
    JarTree8Modules.Common,
    JarTree8Modules.StandaloneApi,
    JarTree8Modules.Protocol,
    JarTree8Modules.Standalone,
    JarTree8Modules.Echo,
    JarTree8Modules.Util,
    JarTree8Modules.Client,
    JarTree8Modules.Installer,
    JarTree8Modules.Packaging,
    JarTree8Modules.App,
    JarTree8Modules.Logging,
    JarTree8Modules.StreamApp,
    JarTree8Modules.Testing,
    Extra8Modules.Shared,
    Extra8Modules.Client,
    Extra8Modules.Server,
    Extra8Modules.Hello,
    Akka8Modules.StateMachine,
    Akka8Modules.Actor,
    Akka8Modules.Stream
  )

  def main(args: Array[String]): Unit = {

    Module.generate(
      Roots,
      Modules
    )

  }

}
