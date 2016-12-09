package org.freedesktop.dbus

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.DBus.ObjectManager")
trait ObjectManager extends org.freedesktop.dbus.DBusInterface {

  def GetManagedObjects() : java.util.Map[org.freedesktop.dbus.DBusInterface, java.util.Map[java.lang.String, java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]]
               
}
           