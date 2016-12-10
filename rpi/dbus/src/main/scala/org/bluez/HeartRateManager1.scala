package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.HeartRateManager1")
trait HeartRateManager1 extends org.freedesktop.dbus.DBusInterface {

  def RegisterWatcher(agent: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
  def UnregisterWatcher(agent: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
}
           