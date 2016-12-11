package org.freedesktop.networkmanager

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.IP4Config")
trait IP4Config extends org.freedesktop.dbus.DBusInterface {


}

object IP4Config extends toolbox8.dbus.common.DBInterfaceCompanion[IP4Config](classOf[IP4Config], "org.freedesktop.NetworkManager.IP4Config") {

  object Props {
    val AddressData = read[java.util.List[java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]]("AddressData")
    val Addresses = read[java.util.List[java.util.List[org.freedesktop.dbus.UInt32]]]("Addresses")
    val DnsOptions = read[java.util.List[java.lang.String]]("DnsOptions")
    val DnsPriority = read[java.lang.Integer]("DnsPriority")
    val Domains = read[java.util.List[java.lang.String]]("Domains")
    val Gateway = read[java.lang.String]("Gateway")
    val Nameservers = read[java.util.List[org.freedesktop.dbus.UInt32]]("Nameservers")
    val RouteData = read[java.util.List[java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]]("RouteData")
    val Routes = read[java.util.List[java.util.List[org.freedesktop.dbus.UInt32]]]("Routes")
    val Searches = read[java.util.List[java.lang.String]]("Searches")
    val WinsServers = read[java.util.List[org.freedesktop.dbus.UInt32]]("WinsServers")
  }

  object Instances {

    object `org.freedesktop.NetworkManager` extends BusInstances("org.freedesktop.NetworkManager") {
      val `/org/freedesktop/NetworkManager/IP4Config/3` = instance("/org/freedesktop/NetworkManager/IP4Config/3")
      val `/org/freedesktop/NetworkManager/IP4Config/2` = instance("/org/freedesktop/NetworkManager/IP4Config/2")
      val `/org/freedesktop/NetworkManager/IP4Config/5` = instance("/org/freedesktop/NetworkManager/IP4Config/5")
      val `/org/freedesktop/NetworkManager/IP4Config/4` = instance("/org/freedesktop/NetworkManager/IP4Config/4")
    }
                 
  }

}
           