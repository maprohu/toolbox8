package org.freedesktop.networkmanager.device

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.Device.Bridge")
trait Bridge extends org.freedesktop.dbus.DBusInterface {


}

object Bridge extends toolbox8.rpi.dbus.DBInterfaceCompanion[Bridge](classOf[Bridge], "org.freedesktop.NetworkManager.Device.Bridge") {

  object Props {
    val Carrier = read[java.lang.Boolean]("Carrier")
    val HwAddress = read[java.lang.String]("HwAddress")
    val Slaves = read[java.util.List[org.freedesktop.dbus.DBusInterface]]("Slaves")
  }

  object Instances {

    object `org.freedesktop.NetworkManager` extends BusInstances("org.freedesktop.NetworkManager") {
      val `/org/freedesktop/NetworkManager/Devices/4` = instance("/org/freedesktop/NetworkManager/Devices/4")
    }
                 
  }

}
           