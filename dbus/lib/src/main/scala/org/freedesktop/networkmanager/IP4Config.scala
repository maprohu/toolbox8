package org.freedesktop.networkmanager

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.IP4Config")
trait IP4Config extends org.freedesktop.dbus.DBusInterface {


}

object IP4Config extends toolbox8.dbus.common.DBInterfaceCompanion[IP4Config](classOf[IP4Config], "org.freedesktop.NetworkManager.IP4Config") {

  object Props {
    val Addresses = read[java.util.List[java.util.List[org.freedesktop.dbus.UInt32]]]("Addresses")
    val Domains = read[java.util.List[java.lang.String]]("Domains")
    val Gateway = read[java.lang.String]("Gateway")
    val Nameservers = read[java.util.List[org.freedesktop.dbus.UInt32]]("Nameservers")
    val Routes = read[java.util.List[java.util.List[org.freedesktop.dbus.UInt32]]]("Routes")
    val Searches = read[java.util.List[java.lang.String]]("Searches")
    val WinsServers = read[java.util.List[org.freedesktop.dbus.UInt32]]("WinsServers")
  }

  object Instances {

    object `org.freedesktop.NetworkManager` extends BusInstances("org.freedesktop.NetworkManager") {
      val `/org/freedesktop/NetworkManager/IP4Config/0` = instance("/org/freedesktop/NetworkManager/IP4Config/0")
    }
                 
  }

}
           