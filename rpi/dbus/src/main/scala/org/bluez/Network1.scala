package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.Network1")
trait Network1 extends org.freedesktop.dbus.DBusInterface {

  def Connect(uuid: java.lang.String) : java.lang.String
               
  def Disconnect() : scala.Unit
               
}
           