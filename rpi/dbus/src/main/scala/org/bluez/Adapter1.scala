package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.Adapter1")
trait Adapter1 extends org.freedesktop.dbus.DBusInterface {

  def StartDiscovery() : scala.Unit
               
  def StopDiscovery() : scala.Unit
               
  def RemoveDevice(device: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
}
           