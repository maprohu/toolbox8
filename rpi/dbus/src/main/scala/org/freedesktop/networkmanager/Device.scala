package org.freedesktop.networkmanager

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.Device")
trait Device extends org.freedesktop.dbus.DBusInterface {

  def Disconnect() : scala.Unit
               
}
           