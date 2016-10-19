package toolbox8.rpi.installer

import toolbox8.rpi.installer.RpiInstaller.Config

/**
  * Created by martonpapp on 19/10/16.
  */
object Rpis {

  implicit val Home = Config(
    host = "192.168.1.36"
  )
  implicit val Tina = Config(
    host = "192.168.2.6"
  )

}
