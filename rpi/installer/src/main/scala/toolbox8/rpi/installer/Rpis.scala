package toolbox8.rpi.installer

import toolbox8.rpi.installer.RpiInstaller.Config

/**
  * Created by martonpapp on 19/10/16.
  */
object Rpis {

  val ClientPort = 5501
  val HomePort = 5502
  val MobilePort = 5503

  implicit val Localhost = Config(
    host = "localhost",
    servicePort = 9978,
    actorSystemName = "jartree"
  )
  implicit val Home = Config(
//    host = "192.168.1.36"
    host = "172.24.1.1",
    akkaPort = HomePort
  )
  implicit val MobileCable = Config(
    host = "10.1.1.49",
    akkaPort = MobilePort
  )
  implicit val MobileHomeWlan = Config(
    host = "192.168.10.215",
    akkaPort = MobilePort
  )

}
