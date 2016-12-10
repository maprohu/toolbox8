package org.freedesktop.networkmanager

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.DHCP4Config")
trait DHCP4Config extends org.freedesktop.dbus.DBusInterface {


}

object DHCP4Config extends toolbox8.rpi.dbus.DBInterfaceCompanion[DHCP4Config](classOf[DHCP4Config], "org.freedesktop.NetworkManager.DHCP4Config") {

  object Props {
    val Options = read[java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]("Options")
  }

  object Instances {

    object `org.freedesktop.NetworkManager` extends BusInstances("org.freedesktop.NetworkManager") {
      val `/org/freedesktop/NetworkManager/DHCP4Config/0` = instance("/org/freedesktop/NetworkManager/DHCP4Config/0")
    }
                 
  }

}
           