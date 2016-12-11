package org.freedesktop.networkmanager

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.AccessPoint")
trait AccessPoint extends org.freedesktop.dbus.DBusInterface {


}

object AccessPoint extends toolbox8.dbus.common.DBInterfaceCompanion[AccessPoint](classOf[AccessPoint], "org.freedesktop.NetworkManager.AccessPoint") {

  object Props {
    val Flags = read[org.freedesktop.dbus.UInt32]("Flags")
    val Frequency = read[org.freedesktop.dbus.UInt32]("Frequency")
    val HwAddress = read[java.lang.String]("HwAddress")
    val LastSeen = read[java.lang.Integer]("LastSeen")
    val MaxBitrate = read[org.freedesktop.dbus.UInt32]("MaxBitrate")
    val Mode = read[org.freedesktop.dbus.UInt32]("Mode")
    val RsnFlags = read[org.freedesktop.dbus.UInt32]("RsnFlags")
    val Ssid = read[java.util.List[java.lang.Byte]]("Ssid")
    val Strength = read[java.lang.Byte]("Strength")
    val WpaFlags = read[org.freedesktop.dbus.UInt32]("WpaFlags")
  }

  object Instances {

    object `org.freedesktop.NetworkManager` extends BusInstances("org.freedesktop.NetworkManager") {
      val `/org/freedesktop/NetworkManager/AccessPoint/3` = instance("/org/freedesktop/NetworkManager/AccessPoint/3")
      val `/org/freedesktop/NetworkManager/AccessPoint/77` = instance("/org/freedesktop/NetworkManager/AccessPoint/77")
      val `/org/freedesktop/NetworkManager/AccessPoint/1` = instance("/org/freedesktop/NetworkManager/AccessPoint/1")
      val `/org/freedesktop/NetworkManager/AccessPoint/0` = instance("/org/freedesktop/NetworkManager/AccessPoint/0")
      val `/org/freedesktop/NetworkManager/AccessPoint/2` = instance("/org/freedesktop/NetworkManager/AccessPoint/2")
      val `/org/freedesktop/NetworkManager/AccessPoint/98` = instance("/org/freedesktop/NetworkManager/AccessPoint/98")
      val `/org/freedesktop/NetworkManager/AccessPoint/106` = instance("/org/freedesktop/NetworkManager/AccessPoint/106")
      val `/org/freedesktop/NetworkManager/AccessPoint/112` = instance("/org/freedesktop/NetworkManager/AccessPoint/112")
      val `/org/freedesktop/NetworkManager/AccessPoint/96` = instance("/org/freedesktop/NetworkManager/AccessPoint/96")
      val `/org/freedesktop/NetworkManager/AccessPoint/111` = instance("/org/freedesktop/NetworkManager/AccessPoint/111")
      val `/org/freedesktop/NetworkManager/AccessPoint/102` = instance("/org/freedesktop/NetworkManager/AccessPoint/102")
      val `/org/freedesktop/NetworkManager/AccessPoint/101` = instance("/org/freedesktop/NetworkManager/AccessPoint/101")
      val `/org/freedesktop/NetworkManager/AccessPoint/67` = instance("/org/freedesktop/NetworkManager/AccessPoint/67")
      val `/org/freedesktop/NetworkManager/AccessPoint/91` = instance("/org/freedesktop/NetworkManager/AccessPoint/91")
      val `/org/freedesktop/NetworkManager/AccessPoint/46` = instance("/org/freedesktop/NetworkManager/AccessPoint/46")
      val `/org/freedesktop/NetworkManager/AccessPoint/37` = instance("/org/freedesktop/NetworkManager/AccessPoint/37")
      val `/org/freedesktop/NetworkManager/AccessPoint/36` = instance("/org/freedesktop/NetworkManager/AccessPoint/36")
      val `/org/freedesktop/NetworkManager/AccessPoint/27` = instance("/org/freedesktop/NetworkManager/AccessPoint/27")
      val `/org/freedesktop/NetworkManager/AccessPoint/19` = instance("/org/freedesktop/NetworkManager/AccessPoint/19")
      val `/org/freedesktop/NetworkManager/AccessPoint/83` = instance("/org/freedesktop/NetworkManager/AccessPoint/83")
      val `/org/freedesktop/NetworkManager/AccessPoint/22` = instance("/org/freedesktop/NetworkManager/AccessPoint/22")
      val `/org/freedesktop/NetworkManager/AccessPoint/16` = instance("/org/freedesktop/NetworkManager/AccessPoint/16")
      val `/org/freedesktop/NetworkManager/AccessPoint/20` = instance("/org/freedesktop/NetworkManager/AccessPoint/20")
      val `/org/freedesktop/NetworkManager/AccessPoint/21` = instance("/org/freedesktop/NetworkManager/AccessPoint/21")
      val `/org/freedesktop/NetworkManager/AccessPoint/13` = instance("/org/freedesktop/NetworkManager/AccessPoint/13")
      val `/org/freedesktop/NetworkManager/AccessPoint/12` = instance("/org/freedesktop/NetworkManager/AccessPoint/12")
      val `/org/freedesktop/NetworkManager/AccessPoint/8` = instance("/org/freedesktop/NetworkManager/AccessPoint/8")
      val `/org/freedesktop/NetworkManager/AccessPoint/5` = instance("/org/freedesktop/NetworkManager/AccessPoint/5")
      val `/org/freedesktop/NetworkManager/AccessPoint/7` = instance("/org/freedesktop/NetworkManager/AccessPoint/7")
    }
                 
  }

}
           