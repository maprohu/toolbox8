package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.HealthManager1")
trait HealthManager1 extends org.freedesktop.dbus.DBusInterface {

  def CreateApplication(config: java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]) : org.freedesktop.dbus.DBusInterface
               
  def DestroyApplication(application: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
}
           