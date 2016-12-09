package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.Media1")
trait Media1 extends org.freedesktop.dbus.DBusInterface {

  def RegisterEndpoint(
    endpoint: org.freedesktop.dbus.DBusInterface,
    properties: java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]
  ) : scala.Unit
               
  def UnregisterEndpoint(endpoint: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
  def RegisterPlayer(
    player: org.freedesktop.dbus.DBusInterface,
    properties: java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]
  ) : scala.Unit
               
  def UnregisterPlayer(player: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
}
           