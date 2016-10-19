package toolbox8.rpi.installer

import java.io.File

/**
  * Created by martonpapp on 19/10/16.
  */
object RunSshCommand {

  def main(args: Array[String]): Unit = {
    import Rpis.Home
    import RpiInstaller._
    implicit val session = connect

    command("which java")
    scp(new File("../sandbox/pom.xml"), "/tmp/pom.xml")
    command("which java")

    session.disconnect()

  }

}
