package org.freedesktop.networkmanager

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.IP6Config")
trait IP6Config extends org.freedesktop.dbus.DBusInterface {


}

object IP6Config extends toolbox8.rpi.dbus.DBInterfaceCompanion[IP6Config](classOf[IP6Config], "org.freedesktop.NetworkManager.IP6Config") {

  object Props {
    val AddressData = read[java.util.List[java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]]("AddressData")
    val Addresses = read[java.util.List[toolbox8.rpi.dbus.DBStruct3[java.util.List[java.lang.Byte], org.freedesktop.dbus.UInt32, java.util.List[java.lang.Byte]]]]("Addresses")
    val DnsOptions = read[java.util.List[java.lang.String]]("DnsOptions")
    val DnsPriority = read[java.lang.Integer]("DnsPriority")
    val Domains = read[java.util.List[java.lang.String]]("Domains")
    val Gateway = read[java.lang.String]("Gateway")
    val Nameservers = read[java.util.List[java.util.List[java.lang.Byte]]]("Nameservers")
    val RouteData = read[java.util.List[java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]]("RouteData")
    val Routes = read[java.util.List[toolbox8.rpi.dbus.DBStruct4[java.util.List[java.lang.Byte], org.freedesktop.dbus.UInt32, java.util.List[java.lang.Byte], org.freedesktop.dbus.UInt32]]]("Routes")
    val Searches = read[java.util.List[java.lang.String]]("Searches")
  }

  object Instances {

    object `org.freedesktop.NetworkManager` extends BusInstances("org.freedesktop.NetworkManager") {
      val `/org/freedesktop/NetworkManager/IP6Config/3` = instance("/org/freedesktop/NetworkManager/IP6Config/3")
      val `/org/freedesktop/NetworkManager/IP6Config/2` = instance("/org/freedesktop/NetworkManager/IP6Config/2")
      val `/org/freedesktop/NetworkManager/IP6Config/5` = instance("/org/freedesktop/NetworkManager/IP6Config/5")
      val `/org/freedesktop/NetworkManager/IP6Config/4` = instance("/org/freedesktop/NetworkManager/IP6Config/4")
    }
                 
  }

}
           