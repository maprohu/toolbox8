package org.freedesktop.networkmanager

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.IP6Config")
trait IP6Config extends org.freedesktop.dbus.DBusInterface {


}

object IP6Config extends toolbox8.dbus.common.DBInterfaceCompanion[IP6Config](classOf[IP6Config], "org.freedesktop.NetworkManager.IP6Config") {

  object Props {
    val Addresses = read[java.util.List[toolbox8.dbus.common.DBStruct3[java.util.List[java.lang.Byte], org.freedesktop.dbus.UInt32, java.util.List[java.lang.Byte]]]]("Addresses")
    val Domains = read[java.util.List[java.lang.String]]("Domains")
    val Gateway = read[java.lang.String]("Gateway")
    val Nameservers = read[java.util.List[java.util.List[java.lang.Byte]]]("Nameservers")
    val Routes = read[java.util.List[toolbox8.dbus.common.DBStruct4[java.util.List[java.lang.Byte], org.freedesktop.dbus.UInt32, java.util.List[java.lang.Byte], org.freedesktop.dbus.UInt32]]]("Routes")
    val Searches = read[java.util.List[java.lang.String]]("Searches")
  }

  object Instances {

    object `org.freedesktop.NetworkManager` extends BusInstances("org.freedesktop.NetworkManager") {
      val `/org/freedesktop/NetworkManager/IP6Config/0` = instance("/org/freedesktop/NetworkManager/IP6Config/0")
    }
                 
  }

}
           