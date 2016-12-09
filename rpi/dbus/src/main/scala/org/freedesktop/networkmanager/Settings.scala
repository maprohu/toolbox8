package org.freedesktop.networkmanager

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.Settings")
trait Settings extends org.freedesktop.dbus.DBusInterface {

  def ListConnections() : java.util.List[org.freedesktop.dbus.DBusInterface]
               
  def GetConnectionByUuid(uuid: java.lang.String) : org.freedesktop.dbus.DBusInterface
               
  def AddConnection(connection: java.util.Map[java.lang.String, java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]) : org.freedesktop.dbus.DBusInterface
               
  def AddConnectionUnsaved(connection: java.util.Map[java.lang.String, java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]) : org.freedesktop.dbus.DBusInterface
               
  def LoadConnections(filenames: java.util.List[java.lang.String]) : toolbox8.rpi.dbus.DBTuple2[java.lang.Boolean, java.util.List[java.lang.String]]
               
  def ReloadConnections() : java.lang.Boolean
               
  def SaveHostname(hostname: java.lang.String) : scala.Unit
               
}
           