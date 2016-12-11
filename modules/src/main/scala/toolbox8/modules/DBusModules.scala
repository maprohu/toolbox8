package toolbox8.modules

import mvnmod.builder.{ScalaModule, SubModuleContainer}
import toolbox6.modules.Toolbox6Modules

/**
  * Created by martonpapp on 31/08/16.
  */
object DBusModules {

  implicit val Container = SubModuleContainer(Toolbox8Modules.Root, "dbus")

  object Common extends ScalaModule(
    "common",
    mvn.`libdbus-java:dbus:jar:2.8`,
    mvn.`libdbus-java:dbus-bin:jar:2.8`,
    mvn.`libunix-java:unix:jar:0.5`,
    mvn.`libmatthew-debug-java:hexdump:jar:0.2`,
    mvn.`libmatthew-debug-java:debug-enable:jar:1.1`
  )

  object Generator extends ScalaModule(
    "generator",
    Common,
    Toolbox6Modules.Logging.R3,
    mvn.`org.scala-lang.modules:scala-xml_2.11:jar:1.0.6`,
    mvn.`org.scala-sbt:io_2.11:jar:1.0.0-M6`
  )

  object Lib extends ScalaModule(
    "lib",
    Common
  )

}
