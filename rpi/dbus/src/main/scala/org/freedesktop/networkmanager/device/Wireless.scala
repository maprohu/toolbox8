package org.freedesktop.networkmanager.device

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.Device.Wireless")
trait Wireless extends org.freedesktop.dbus.DBusInterface {

  def GetAccessPoints() : java.util.List[org.freedesktop.dbus.DBusInterface]
               
  def GetAllAccessPoints() : java.util.List[org.freedesktop.dbus.DBusInterface]
               
  def RequestScan(options: java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]) : scala.Unit
               
}

object Wireless extends toolbox8.rpi.dbus.DBInterfaceCompanion[Wireless](classOf[Wireless], "org.freedesktop.NetworkManager.Device.Wireless") {

  object Props {
    val AccessPoints = read[java.util.List[org.freedesktop.dbus.DBusInterface]]("AccessPoints")
    val ActiveAccessPoint = read[org.freedesktop.dbus.DBusInterface]("ActiveAccessPoint")
    val Bitrate = read[org.freedesktop.dbus.UInt32]("Bitrate")
    val HwAddress = read[java.lang.String]("HwAddress")
    val Mode = read[org.freedesktop.dbus.UInt32]("Mode")
    val PermHwAddress = read[java.lang.String]("PermHwAddress")
    val WirelessCapabilities = read[org.freedesktop.dbus.UInt32]("WirelessCapabilities")
  }

  object Instances {

    object `org.freedesktop.NetworkManager` extends BusInstances("org.freedesktop.NetworkManager") {
      val `/org/freedesktop/NetworkManager/Devices/0` = instance("/org/freedesktop/NetworkManager/Devices/0")
    }
                 
  }

}
           