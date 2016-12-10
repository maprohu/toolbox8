package org.freedesktop.networkmanager.device

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.Device.Wireless")
trait Wireless extends org.freedesktop.dbus.DBusInterface {

  def RequestScan(options: java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]) : scala.Unit
               
  def GetAllAccessPoints() : java.util.List[org.freedesktop.dbus.DBusInterface]
               
  def GetAccessPoints() : java.util.List[org.freedesktop.dbus.DBusInterface]
               
}
           