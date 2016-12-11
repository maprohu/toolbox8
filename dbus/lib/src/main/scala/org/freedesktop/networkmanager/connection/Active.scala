package org.freedesktop.networkmanager.connection

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.Connection.Active")
trait Active extends org.freedesktop.dbus.DBusInterface {


}

object Active extends toolbox8.dbus.common.DBInterfaceCompanion[Active](classOf[Active], "org.freedesktop.NetworkManager.Connection.Active") {

  object Props {
    val Connection = read[org.freedesktop.dbus.DBusInterface]("Connection")
    val Default = read[java.lang.Boolean]("Default")
    val Default6 = read[java.lang.Boolean]("Default6")
    val Devices = read[java.util.List[org.freedesktop.dbus.DBusInterface]]("Devices")
    val Dhcp4Config = read[org.freedesktop.dbus.DBusInterface]("Dhcp4Config")
    val Dhcp6Config = read[org.freedesktop.dbus.DBusInterface]("Dhcp6Config")
    val Id = read[java.lang.String]("Id")
    val Ip4Config = read[org.freedesktop.dbus.DBusInterface]("Ip4Config")
    val Ip6Config = read[org.freedesktop.dbus.DBusInterface]("Ip6Config")
    val Master = read[org.freedesktop.dbus.DBusInterface]("Master")
    val SpecificObject = read[org.freedesktop.dbus.DBusInterface]("SpecificObject")
    val State = read[org.freedesktop.dbus.UInt32]("State")
    val Type = read[java.lang.String]("Type")
    val Uuid = read[java.lang.String]("Uuid")
    val Vpn = read[java.lang.Boolean]("Vpn")
  }

  object Instances {

    object `org.freedesktop.NetworkManager` extends BusInstances("org.freedesktop.NetworkManager") {
      val `/org/freedesktop/NetworkManager/ActiveConnection/1` = instance("/org/freedesktop/NetworkManager/ActiveConnection/1")
      val `/org/freedesktop/NetworkManager/ActiveConnection/0` = instance("/org/freedesktop/NetworkManager/ActiveConnection/0")
    }
                 
  }

}
           