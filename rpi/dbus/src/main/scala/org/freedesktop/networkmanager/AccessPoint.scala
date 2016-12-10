package org.freedesktop.networkmanager

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.AccessPoint")
trait AccessPoint extends org.freedesktop.dbus.DBusInterface {


}

object AccessPoint extends toolbox8.rpi.dbus.DBInterfaceCompanion[AccessPoint](classOf[AccessPoint], "org.freedesktop.NetworkManager.AccessPoint") {

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
      val `/org/freedesktop/NetworkManager/AccessPoint/24` = instance("/org/freedesktop/NetworkManager/AccessPoint/24")
      val `/org/freedesktop/NetworkManager/AccessPoint/138` = instance("/org/freedesktop/NetworkManager/AccessPoint/138")
      val `/org/freedesktop/NetworkManager/AccessPoint/9` = instance("/org/freedesktop/NetworkManager/AccessPoint/9")
      val `/org/freedesktop/NetworkManager/AccessPoint/22` = instance("/org/freedesktop/NetworkManager/AccessPoint/22")
      val `/org/freedesktop/NetworkManager/AccessPoint/6` = instance("/org/freedesktop/NetworkManager/AccessPoint/6")
      val `/org/freedesktop/NetworkManager/AccessPoint/133` = instance("/org/freedesktop/NetworkManager/AccessPoint/133")
      val `/org/freedesktop/NetworkManager/AccessPoint/3` = instance("/org/freedesktop/NetworkManager/AccessPoint/3")
      val `/org/freedesktop/NetworkManager/AccessPoint/1` = instance("/org/freedesktop/NetworkManager/AccessPoint/1")
      val `/org/freedesktop/NetworkManager/AccessPoint/0` = instance("/org/freedesktop/NetworkManager/AccessPoint/0")
      val `/org/freedesktop/NetworkManager/AccessPoint/187` = instance("/org/freedesktop/NetworkManager/AccessPoint/187")
      val `/org/freedesktop/NetworkManager/AccessPoint/186` = instance("/org/freedesktop/NetworkManager/AccessPoint/186")
      val `/org/freedesktop/NetworkManager/AccessPoint/185` = instance("/org/freedesktop/NetworkManager/AccessPoint/185")
      val `/org/freedesktop/NetworkManager/AccessPoint/180` = instance("/org/freedesktop/NetworkManager/AccessPoint/180")
      val `/org/freedesktop/NetworkManager/AccessPoint/148` = instance("/org/freedesktop/NetworkManager/AccessPoint/148")
      val `/org/freedesktop/NetworkManager/AccessPoint/19` = instance("/org/freedesktop/NetworkManager/AccessPoint/19")
      val `/org/freedesktop/NetworkManager/AccessPoint/30` = instance("/org/freedesktop/NetworkManager/AccessPoint/30")
      val `/org/freedesktop/NetworkManager/AccessPoint/126` = instance("/org/freedesktop/NetworkManager/AccessPoint/126")
      val `/org/freedesktop/NetworkManager/AccessPoint/11` = instance("/org/freedesktop/NetworkManager/AccessPoint/11")
      val `/org/freedesktop/NetworkManager/AccessPoint/83` = instance("/org/freedesktop/NetworkManager/AccessPoint/83")
      val `/org/freedesktop/NetworkManager/AccessPoint/199` = instance("/org/freedesktop/NetworkManager/AccessPoint/199")
      val `/org/freedesktop/NetworkManager/AccessPoint/208` = instance("/org/freedesktop/NetworkManager/AccessPoint/208")
      val `/org/freedesktop/NetworkManager/AccessPoint/197` = instance("/org/freedesktop/NetworkManager/AccessPoint/197")
      val `/org/freedesktop/NetworkManager/AccessPoint/207` = instance("/org/freedesktop/NetworkManager/AccessPoint/207")
      val `/org/freedesktop/NetworkManager/AccessPoint/206` = instance("/org/freedesktop/NetworkManager/AccessPoint/206")
      val `/org/freedesktop/NetworkManager/AccessPoint/205` = instance("/org/freedesktop/NetworkManager/AccessPoint/205")
      val `/org/freedesktop/NetworkManager/AccessPoint/204` = instance("/org/freedesktop/NetworkManager/AccessPoint/204")
      val `/org/freedesktop/NetworkManager/AccessPoint/177` = instance("/org/freedesktop/NetworkManager/AccessPoint/177")
      val `/org/freedesktop/NetworkManager/AccessPoint/191` = instance("/org/freedesktop/NetworkManager/AccessPoint/191")
      val `/org/freedesktop/NetworkManager/AccessPoint/201` = instance("/org/freedesktop/NetworkManager/AccessPoint/201")
      val `/org/freedesktop/NetworkManager/AccessPoint/173` = instance("/org/freedesktop/NetworkManager/AccessPoint/173")
      val `/org/freedesktop/NetworkManager/AccessPoint/8` = instance("/org/freedesktop/NetworkManager/AccessPoint/8")
    }
                 
  }

}
           