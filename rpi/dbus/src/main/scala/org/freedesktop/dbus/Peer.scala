package org.freedesktop.dbus

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.DBus.Peer")
trait Peer extends org.freedesktop.dbus.DBusInterface {

  def Ping() : scala.Unit
               
  def GetMachineId() : java.lang.String
               
}
           