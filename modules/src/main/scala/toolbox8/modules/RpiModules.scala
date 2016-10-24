package toolbox8.modules

import maven.modules.builder.{ScalaModule, SubModuleContainer}
import toolbox6.modules.Toolbox6Modules

/**
  * Created by martonpapp on 31/08/16.
  */
object RpiModules {

  implicit val Container = SubModuleContainer(Toolbox8Modules.Root, "rpi")


  object Installer extends ScalaModule(
    "installer",
    mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.7.7`,
    mvn.`com.jcraft:jsch:jar:0.1.54`,
    JarTree8Modules.Packaging,
    Toolbox8Modules
  )

  object Remote extends ScalaModule(
    "remote"
  )

  object  Bluetooth extends ScalaModule(
    "bluetooth",
    mvn.`net.sf.bluecove:bluecove:jar:2.1.0`,
    mvn.`net.sf.bluecove:bluecove-gpl:jar:2.1.0`
  )



  object DBus extends ScalaModule(
    "dbus",
    mvn.`libdbus-java:dbus:jar:2.8`,
    mvn.`libunix-java:unix:jar:0.5`,
    mvn.`libmatthew-debug-java:hexdump:jar:0.2`,
    mvn.`libmatthew-debug-java:debug-enable:jar:1.1`,
    mvn.`org.scala-lang.modules:scala-xml_2.11:jar:1.0.6`,
    Toolbox6Modules.Macros

    //    VoiceModules,
    //    JarTree8Modules.Standalone

  )
}
