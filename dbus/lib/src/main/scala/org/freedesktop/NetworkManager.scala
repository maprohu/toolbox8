package org.freedesktop

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager")
trait NetworkManager extends org.freedesktop.dbus.DBusInterface {

  def ActivateConnection(
    connection: org.freedesktop.dbus.DBusInterface,
    device: org.freedesktop.dbus.DBusInterface,
    specific_object: org.freedesktop.dbus.DBusInterface
  ) : org.freedesktop.dbus.DBusInterface
               
  def AddAndActivateConnection(
    connection: java.util.Map[java.lang.String, java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]],
    device: org.freedesktop.dbus.DBusInterface,
    specific_object: org.freedesktop.dbus.DBusInterface
  ) : toolbox8.dbus.common.DBTuple2[org.freedesktop.dbus.DBusInterface, org.freedesktop.dbus.DBusInterface]
               
  def CheckConnectivity() : org.freedesktop.dbus.UInt32
               
  def DeactivateConnection(active_connection: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
  def Enable(enable: java.lang.Boolean) : scala.Unit
               
  def GetAllDevices() : java.util.List[org.freedesktop.dbus.DBusInterface]
               
  def GetDeviceByIpIface(iface: java.lang.String) : org.freedesktop.dbus.DBusInterface
               
  def GetDevices() : java.util.List[org.freedesktop.dbus.DBusInterface]
               
  def GetLogging() : toolbox8.dbus.common.DBTuple2[java.lang.String, java.lang.String]
               
  def GetPermissions() : java.util.Map[java.lang.String, java.lang.String]
               
  def Reload(flags: org.freedesktop.dbus.UInt32) : scala.Unit
               
  def SetLogging(
    level: java.lang.String,
    domains: java.lang.String
  ) : scala.Unit
               
  def Sleep(sleep: java.lang.Boolean) : scala.Unit
               
  def state() : org.freedesktop.dbus.UInt32
               
}

object NetworkManager extends toolbox8.dbus.common.DBInterfaceCompanion[NetworkManager](classOf[NetworkManager], "org.freedesktop.NetworkManager") {

  object Props {
    val ActivatingConnection = read[org.freedesktop.dbus.DBusInterface]("ActivatingConnection")
    val ActiveConnections = read[java.util.List[org.freedesktop.dbus.DBusInterface]]("ActiveConnections")
    val AllDevices = read[java.util.List[org.freedesktop.dbus.DBusInterface]]("AllDevices")
    val Connectivity = read[org.freedesktop.dbus.UInt32]("Connectivity")
    val Devices = read[java.util.List[org.freedesktop.dbus.DBusInterface]]("Devices")
    val GlobalDnsConfiguration = readwrite[java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]("GlobalDnsConfiguration")
    val Metered = read[org.freedesktop.dbus.UInt32]("Metered")
    val NetworkingEnabled = read[java.lang.Boolean]("NetworkingEnabled")
    val PrimaryConnection = read[org.freedesktop.dbus.DBusInterface]("PrimaryConnection")
    val PrimaryConnectionType = read[java.lang.String]("PrimaryConnectionType")
    val Startup = read[java.lang.Boolean]("Startup")
    val State = read[org.freedesktop.dbus.UInt32]("State")
    val Version = read[java.lang.String]("Version")
    val WimaxEnabled = readwrite[java.lang.Boolean]("WimaxEnabled")
    val WimaxHardwareEnabled = read[java.lang.Boolean]("WimaxHardwareEnabled")
    val WirelessEnabled = readwrite[java.lang.Boolean]("WirelessEnabled")
    val WirelessHardwareEnabled = read[java.lang.Boolean]("WirelessHardwareEnabled")
    val WwanEnabled = readwrite[java.lang.Boolean]("WwanEnabled")
    val WwanHardwareEnabled = read[java.lang.Boolean]("WwanHardwareEnabled")
  }

  object Instances {

    object `org.freedesktop.NetworkManager` extends BusInstances("org.freedesktop.NetworkManager") {
      val `/org/freedesktop/NetworkManager` = instance("/org/freedesktop/NetworkManager")
    }
                 
  }

}
           