package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.ProfileManager1")
trait ProfileManager1 extends org.freedesktop.dbus.DBusInterface {

  def RegisterProfile(
    profile: org.freedesktop.dbus.DBusInterface,
    UUID: java.lang.String,
    options: java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]
  ) : scala.Unit
               
  def UnregisterProfile(profile: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
}
           