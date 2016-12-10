package org.freedesktop.networkmanager.device

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.Device.Bluetooth")
trait Bluetooth extends org.freedesktop.dbus.DBusInterface {


}

object Bluetooth extends toolbox8.rpi.dbus.DBInterfaceCompanion[Bluetooth](classOf[Bluetooth], "org.freedesktop.NetworkManager.Device.Bluetooth") {

  object Props {
    val BtCapabilities = read[org.freedesktop.dbus.UInt32]("BtCapabilities")
    val HwAddress = read[java.lang.String]("HwAddress")
    val Name = read[java.lang.String]("Name")
  }

  object Instances {

    object `org.freedesktop.NetworkManager` extends BusInstances("org.freedesktop.NetworkManager") {
      val `/org/freedesktop/NetworkManager/Devices/3` = instance("/org/freedesktop/NetworkManager/Devices/3")
    }
                 
  }

}
           