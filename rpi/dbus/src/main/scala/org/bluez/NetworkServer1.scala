package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.NetworkServer1")
trait NetworkServer1 extends org.freedesktop.dbus.DBusInterface {

  def Register(
    uuid: java.lang.String,
    bridge: java.lang.String
  ) : scala.Unit
               
  def Unregister(uuid: java.lang.String) : scala.Unit
               
}
           