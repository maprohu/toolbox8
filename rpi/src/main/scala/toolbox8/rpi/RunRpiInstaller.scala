package toolbox8.rpi

import toolbox8.rpi.RpiInstaller.Config

/**
  * Created by martonpapp on 15/10/16.
  */
object RunRpiInstaller {

  val Packages = Seq(
    "oracle-java8-jdk"
  )


  def main(args: Array[String]): Unit = {
    implicit val config = Config(
      host = "192.168.2.6"
    )

    RpiInstaller.run(
//      "apt-cache search jdk"
      s"sudo apt-get -y install ${Packages.mkString(" ")}"
    )
  }

}
