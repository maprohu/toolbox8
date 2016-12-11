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
      val `/org/freedesktop/NetworkManager/AccessPoint/7` = instance("/org/freedesktop/NetworkManager/AccessPoint/7")
      val `/org/freedesktop/NetworkManager/AccessPoint/6` = instance("/org/freedesktop/NetworkManager/AccessPoint/6")
      val `/org/freedesktop/NetworkManager/AccessPoint/24` = instance("/org/freedesktop/NetworkManager/AccessPoint/24")
      val `/org/freedesktop/NetworkManager/AccessPoint/23` = instance("/org/freedesktop/NetworkManager/AccessPoint/23")
      val `/org/freedesktop/NetworkManager/AccessPoint/9` = instance("/org/freedesktop/NetworkManager/AccessPoint/9")
      val `/org/freedesktop/NetworkManager/AccessPoint/22` = instance("/org/freedesktop/NetworkManager/AccessPoint/22")
      val `/org/freedesktop/NetworkManager/AccessPoint/21` = instance("/org/freedesktop/NetworkManager/AccessPoint/21")
      val `/org/freedesktop/NetworkManager/AccessPoint/20` = instance("/org/freedesktop/NetworkManager/AccessPoint/20")
      val `/org/freedesktop/NetworkManager/AccessPoint/5` = instance("/org/freedesktop/NetworkManager/AccessPoint/5")
      val `/org/freedesktop/NetworkManager/AccessPoint/4` = instance("/org/freedesktop/NetworkManager/AccessPoint/4")
      val `/org/freedesktop/NetworkManager/AccessPoint/3` = instance("/org/freedesktop/NetworkManager/AccessPoint/3")
      val `/org/freedesktop/NetworkManager/AccessPoint/2` = instance("/org/freedesktop/NetworkManager/AccessPoint/2")
      val `/org/freedesktop/NetworkManager/AccessPoint/1` = instance("/org/freedesktop/NetworkManager/AccessPoint/1")
      val `/org/freedesktop/NetworkManager/AccessPoint/0` = instance("/org/freedesktop/NetworkManager/AccessPoint/0")
      val `/org/freedesktop/NetworkManager/AccessPoint/37` = instance("/org/freedesktop/NetworkManager/AccessPoint/37")
      val `/org/freedesktop/NetworkManager/AccessPoint/59` = instance("/org/freedesktop/NetworkManager/AccessPoint/59")
      val `/org/freedesktop/NetworkManager/AccessPoint/58` = instance("/org/freedesktop/NetworkManager/AccessPoint/58")
      val `/org/freedesktop/NetworkManager/AccessPoint/55` = instance("/org/freedesktop/NetworkManager/AccessPoint/55")
      val `/org/freedesktop/NetworkManager/AccessPoint/54` = instance("/org/freedesktop/NetworkManager/AccessPoint/54")
      val `/org/freedesktop/NetworkManager/AccessPoint/32` = instance("/org/freedesktop/NetworkManager/AccessPoint/32")
      val `/org/freedesktop/NetworkManager/AccessPoint/52` = instance("/org/freedesktop/NetworkManager/AccessPoint/52")
      val `/org/freedesktop/NetworkManager/AccessPoint/50` = instance("/org/freedesktop/NetworkManager/AccessPoint/50")
      val `/org/freedesktop/NetworkManager/AccessPoint/18` = instance("/org/freedesktop/NetworkManager/AccessPoint/18")
      val `/org/freedesktop/NetworkManager/AccessPoint/17` = instance("/org/freedesktop/NetworkManager/AccessPoint/17")
      val `/org/freedesktop/NetworkManager/AccessPoint/16` = instance("/org/freedesktop/NetworkManager/AccessPoint/16")
      val `/org/freedesktop/NetworkManager/AccessPoint/15` = instance("/org/freedesktop/NetworkManager/AccessPoint/15")
      val `/org/freedesktop/NetworkManager/AccessPoint/14` = instance("/org/freedesktop/NetworkManager/AccessPoint/14")
      val `/org/freedesktop/NetworkManager/AccessPoint/13` = instance("/org/freedesktop/NetworkManager/AccessPoint/13")
      val `/org/freedesktop/NetworkManager/AccessPoint/49` = instance("/org/freedesktop/NetworkManager/AccessPoint/49")
      val `/org/freedesktop/NetworkManager/AccessPoint/48` = instance("/org/freedesktop/NetworkManager/AccessPoint/48")
      val `/org/freedesktop/NetworkManager/AccessPoint/43` = instance("/org/freedesktop/NetworkManager/AccessPoint/43")
      val `/org/freedesktop/NetworkManager/AccessPoint/26` = instance("/org/freedesktop/NetworkManager/AccessPoint/26")
      val `/org/freedesktop/NetworkManager/AccessPoint/41` = instance("/org/freedesktop/NetworkManager/AccessPoint/41")
      val `/org/freedesktop/NetworkManager/AccessPoint/40` = instance("/org/freedesktop/NetworkManager/AccessPoint/40")
    }
                 
  }

}
           