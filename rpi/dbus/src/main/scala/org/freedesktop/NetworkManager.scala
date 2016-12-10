package org.freedesktop

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager")
trait NetworkManager extends org.freedesktop.dbus.DBusInterface {

  def state() : org.freedesktop.dbus.UInt32
               
  def CheckConnectivity() : org.freedesktop.dbus.UInt32
               
  def GetLogging() : toolbox8.rpi.dbus.DBTuple2[java.lang.String, java.lang.String]
               
  def SetLogging(
    level: java.lang.String,
    domains: java.lang.String
  ) : scala.Unit
               
  def GetPermissions() : java.util.Map[java.lang.String, java.lang.String]
               
  def Enable(enable: java.lang.Boolean) : scala.Unit
               
  def Sleep(sleep: java.lang.Boolean) : scala.Unit
               
  def DeactivateConnection(active_connection: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
  def AddAndActivateConnection(
    connection: java.util.Map[java.lang.String, java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]],
    device: org.freedesktop.dbus.DBusInterface,
    specific_object: org.freedesktop.dbus.DBusInterface
  ) : toolbox8.rpi.dbus.DBTuple2[org.freedesktop.dbus.DBusInterface, org.freedesktop.dbus.DBusInterface]
               
  def ActivateConnection(
    connection: org.freedesktop.dbus.DBusInterface,
    device: org.freedesktop.dbus.DBusInterface,
    specific_object: org.freedesktop.dbus.DBusInterface
  ) : org.freedesktop.dbus.DBusInterface
               
  def GetDeviceByIpIface(iface: java.lang.String) : org.freedesktop.dbus.DBusInterface
               
  def GetDevices() : java.util.List[org.freedesktop.dbus.DBusInterface]
               
}
           