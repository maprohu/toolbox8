package org.freedesktop.networkmanager.device

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.Device.Wired")
trait Wired extends org.freedesktop.dbus.DBusInterface {


}

object Wired extends toolbox8.dbus.common.DBInterfaceCompanion[Wired](classOf[Wired], "org.freedesktop.NetworkManager.Device.Wired") {

  object Props {
    val Carrier = read[java.lang.Boolean]("Carrier")
    val HwAddress = read[java.lang.String]("HwAddress")
    val PermHwAddress = read[java.lang.String]("PermHwAddress")
    val Speed = read[org.freedesktop.dbus.UInt32]("Speed")
  }

  object Instances {

    object `org.freedesktop.NetworkManager` extends BusInstances("org.freedesktop.NetworkManager") {
      val `/org/freedesktop/NetworkManager/Devices/1` = instance("/org/freedesktop/NetworkManager/Devices/1")
    }
                 
  }

}
           