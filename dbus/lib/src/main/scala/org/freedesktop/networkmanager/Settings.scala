package org.freedesktop.networkmanager

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.Settings")
trait Settings extends org.freedesktop.dbus.DBusInterface {

  def AddConnection(connection: java.util.Map[java.lang.String, java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]) : org.freedesktop.dbus.DBusInterface
               
  def AddConnectionUnsaved(connection: java.util.Map[java.lang.String, java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]) : org.freedesktop.dbus.DBusInterface
               
  def GetConnectionByUuid(uuid: java.lang.String) : org.freedesktop.dbus.DBusInterface
               
  def ListConnections() : java.util.List[org.freedesktop.dbus.DBusInterface]
               
  def LoadConnections(filenames: java.util.List[java.lang.String]) : toolbox8.dbus.common.DBTuple2[java.lang.Boolean, java.util.List[java.lang.String]]
               
  def ReloadConnections() : java.lang.Boolean
               
  def SaveHostname(hostname: java.lang.String) : scala.Unit
               
}

object Settings extends toolbox8.dbus.common.DBInterfaceCompanion[Settings](classOf[Settings], "org.freedesktop.NetworkManager.Settings") {

  object Props {
    val CanModify = read[java.lang.Boolean]("CanModify")
    val Connections = read[java.util.List[org.freedesktop.dbus.DBusInterface]]("Connections")
    val Hostname = read[java.lang.String]("Hostname")
  }

  object Instances {

    object `org.freedesktop.NetworkManager` extends BusInstances("org.freedesktop.NetworkManager") {
      val `/org/freedesktop/NetworkManager/Settings` = instance("/org/freedesktop/NetworkManager/Settings")
    }
                 
  }

}
           