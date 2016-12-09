package org.freedesktop.networkmanager

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.Device")
trait Device extends org.freedesktop.dbus.DBusInterface {

  def Reapply(
    connection: java.util.Map[java.lang.String, java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]],
    version_id: org.freedesktop.dbus.UInt64,
    flags: org.freedesktop.dbus.UInt32
  ) : scala.Unit
               
  def GetAppliedConnection(flags: org.freedesktop.dbus.UInt32) : toolbox8.rpi.dbus.DBTuple2[java.util.Map[java.lang.String, java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]], org.freedesktop.dbus.UInt64]
               
  def Disconnect() : scala.Unit
               
  def Delete() : scala.Unit
               
}
           