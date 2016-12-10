package org.freedesktop.networkmanager

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.Device")
trait Device extends org.freedesktop.dbus.DBusInterface {

  def Delete() : scala.Unit
               
  def Disconnect() : scala.Unit
               
  def GetAppliedConnection(flags: org.freedesktop.dbus.UInt32) : toolbox8.rpi.dbus.DBTuple2[java.util.Map[java.lang.String, java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]], org.freedesktop.dbus.UInt64]
               
  def Reapply(
    connection: java.util.Map[java.lang.String, java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]],
    version_id: org.freedesktop.dbus.UInt64,
    flags: org.freedesktop.dbus.UInt32
  ) : scala.Unit
               
}

object Device extends toolbox8.rpi.dbus.DBInterfaceCompanion[Device](classOf[Device], "org.freedesktop.NetworkManager.Device") {

  object Props {
    val ActiveConnection = read[org.freedesktop.dbus.DBusInterface]("ActiveConnection")
    val Autoconnect = readwrite[java.lang.Boolean]("Autoconnect")
    val AvailableConnections = read[java.util.List[org.freedesktop.dbus.DBusInterface]]("AvailableConnections")
    val Capabilities = read[org.freedesktop.dbus.UInt32]("Capabilities")
    val DeviceType = read[org.freedesktop.dbus.UInt32]("DeviceType")
    val Dhcp4Config = read[org.freedesktop.dbus.DBusInterface]("Dhcp4Config")
    val Dhcp6Config = read[org.freedesktop.dbus.DBusInterface]("Dhcp6Config")
    val Driver = read[java.lang.String]("Driver")
    val DriverVersion = read[java.lang.String]("DriverVersion")
    val FirmwareMissing = read[java.lang.Boolean]("FirmwareMissing")
    val FirmwareVersion = read[java.lang.String]("FirmwareVersion")
    val Interface = read[java.lang.String]("Interface")
    val Ip4Address = read[org.freedesktop.dbus.UInt32]("Ip4Address")
    val Ip4Config = read[org.freedesktop.dbus.DBusInterface]("Ip4Config")
    val Ip6Config = read[org.freedesktop.dbus.DBusInterface]("Ip6Config")
    val IpInterface = read[java.lang.String]("IpInterface")
    val LldpNeighbors = read[java.util.List[java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]]("LldpNeighbors")
    val Managed = readwrite[java.lang.Boolean]("Managed")
    val Metered = read[org.freedesktop.dbus.UInt32]("Metered")
    val Mtu = read[org.freedesktop.dbus.UInt32]("Mtu")
    val NmPluginMissing = read[java.lang.Boolean]("NmPluginMissing")
    val PhysicalPortId = read[java.lang.String]("PhysicalPortId")
    val Real = read[java.lang.Boolean]("Real")
    val State = read[org.freedesktop.dbus.UInt32]("State")
    val StateReason = read[toolbox8.rpi.dbus.DBStruct2[org.freedesktop.dbus.UInt32, org.freedesktop.dbus.UInt32]]("StateReason")
    val Udi = read[java.lang.String]("Udi")
  }

  object Instances {

    object `org.freedesktop.NetworkManager` extends BusInstances("org.freedesktop.NetworkManager") {
      val `/org/freedesktop/NetworkManager/Devices/3` = instance("/org/freedesktop/NetworkManager/Devices/3")
      val `/org/freedesktop/NetworkManager/Devices/2` = instance("/org/freedesktop/NetworkManager/Devices/2")
      val `/org/freedesktop/NetworkManager/Devices/1` = instance("/org/freedesktop/NetworkManager/Devices/1")
      val `/org/freedesktop/NetworkManager/Devices/0` = instance("/org/freedesktop/NetworkManager/Devices/0")
      val `/org/freedesktop/NetworkManager/Devices/4` = instance("/org/freedesktop/NetworkManager/Devices/4")
    }
                 
  }

}
           