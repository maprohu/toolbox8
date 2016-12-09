package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.Alert1")
trait Alert1 extends org.freedesktop.dbus.DBusInterface {

  def RegisterAlert(
    category: java.lang.String,
    agent: org.freedesktop.dbus.DBusInterface
  ) : scala.Unit
               
  def NewAlert(
    category: java.lang.String,
    count: org.freedesktop.dbus.UInt16,
    description: java.lang.String
  ) : scala.Unit
               
  def UnreadAlert(
    category: java.lang.String,
    count: org.freedesktop.dbus.UInt16
  ) : scala.Unit
               
}
           