package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.ThermometerManager1")
trait ThermometerManager1 extends org.freedesktop.dbus.DBusInterface {

  def RegisterWatcher(agent: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
  def UnregisterWatcher(agent: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
  def EnableIntermediateMeasurement(agent: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
  def DisableIntermediateMeasurement(agent: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
}
           