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
      val `/org/freedesktop/NetworkManager/AccessPoint/8` = instance("/org/freedesktop/NetworkManager/AccessPoint/8")
      val `/org/freedesktop/NetworkManager/AccessPoint/7` = instance("/org/freedesktop/NetworkManager/AccessPoint/7")
      val `/org/freedesktop/NetworkManager/AccessPoint/6` = instance("/org/freedesktop/NetworkManager/AccessPoint/6")
      val `/org/freedesktop/NetworkManager/AccessPoint/5` = instance("/org/freedesktop/NetworkManager/AccessPoint/5")
      val `/org/freedesktop/NetworkManager/AccessPoint/4` = instance("/org/freedesktop/NetworkManager/AccessPoint/4")
      val `/org/freedesktop/NetworkManager/AccessPoint/2` = instance("/org/freedesktop/NetworkManager/AccessPoint/2")
      val `/org/freedesktop/NetworkManager/AccessPoint/71` = instance("/org/freedesktop/NetworkManager/AccessPoint/71")
      val `/org/freedesktop/NetworkManager/AccessPoint/1` = instance("/org/freedesktop/NetworkManager/AccessPoint/1")
      val `/org/freedesktop/NetworkManager/AccessPoint/0` = instance("/org/freedesktop/NetworkManager/AccessPoint/0")
      val `/org/freedesktop/NetworkManager/AccessPoint/64` = instance("/org/freedesktop/NetworkManager/AccessPoint/64")
      val `/org/freedesktop/NetworkManager/AccessPoint/69` = instance("/org/freedesktop/NetworkManager/AccessPoint/69")
      val `/org/freedesktop/NetworkManager/AccessPoint/46` = instance("/org/freedesktop/NetworkManager/AccessPoint/46")
      val `/org/freedesktop/NetworkManager/AccessPoint/61` = instance("/org/freedesktop/NetworkManager/AccessPoint/61")
      val `/org/freedesktop/NetworkManager/AccessPoint/58` = instance("/org/freedesktop/NetworkManager/AccessPoint/58")
      val `/org/freedesktop/NetworkManager/AccessPoint/65` = instance("/org/freedesktop/NetworkManager/AccessPoint/65")
      val `/org/freedesktop/NetworkManager/AccessPoint/56` = instance("/org/freedesktop/NetworkManager/AccessPoint/56")
      val `/org/freedesktop/NetworkManager/AccessPoint/70` = instance("/org/freedesktop/NetworkManager/AccessPoint/70")
      val `/org/freedesktop/NetworkManager/AccessPoint/54` = instance("/org/freedesktop/NetworkManager/AccessPoint/54")
      val `/org/freedesktop/NetworkManager/AccessPoint/26` = instance("/org/freedesktop/NetworkManager/AccessPoint/26")
      val `/org/freedesktop/NetworkManager/AccessPoint/25` = instance("/org/freedesktop/NetworkManager/AccessPoint/25")
      val `/org/freedesktop/NetworkManager/AccessPoint/19` = instance("/org/freedesktop/NetworkManager/AccessPoint/19")
      val `/org/freedesktop/NetworkManager/AccessPoint/18` = instance("/org/freedesktop/NetworkManager/AccessPoint/18")
      val `/org/freedesktop/NetworkManager/AccessPoint/17` = instance("/org/freedesktop/NetworkManager/AccessPoint/17")
      val `/org/freedesktop/NetworkManager/AccessPoint/24` = instance("/org/freedesktop/NetworkManager/AccessPoint/24")
      val `/org/freedesktop/NetworkManager/AccessPoint/47` = instance("/org/freedesktop/NetworkManager/AccessPoint/47")
      val `/org/freedesktop/NetworkManager/AccessPoint/21` = instance("/org/freedesktop/NetworkManager/AccessPoint/21")
      val `/org/freedesktop/NetworkManager/AccessPoint/13` = instance("/org/freedesktop/NetworkManager/AccessPoint/13")
      val `/org/freedesktop/NetworkManager/AccessPoint/12` = instance("/org/freedesktop/NetworkManager/AccessPoint/12")
      val `/org/freedesktop/NetworkManager/AccessPoint/10` = instance("/org/freedesktop/NetworkManager/AccessPoint/10")
      val `/org/freedesktop/NetworkManager/AccessPoint/9` = instance("/org/freedesktop/NetworkManager/AccessPoint/9")
    }
                 
  }

}
           