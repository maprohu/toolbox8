package toolbox8.rpi.installer

import toolbox8.rpi.installer.RpiInstaller.Config

/**
  * Created by martonpapp on 19/10/16.
  */
object Rpis {


  implicit val Localhost = Config(
    host = "localhost",
    port = 9978
  )
  implicit val Home = Config(
    host = "192.168.1.36"
  )
  implicit val MobileCable = Config(
    host = "10.1.1.49"
  )
  implicit val MobileHomeWlan = Config(
    host = "192.168.10.215"
  )

}
