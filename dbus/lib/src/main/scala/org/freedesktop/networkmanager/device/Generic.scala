package org.freedesktop.networkmanager.device

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.Device.Generic")
trait Generic extends org.freedesktop.dbus.DBusInterface {


}

object Generic extends toolbox8.dbus.common.DBInterfaceCompanion[Generic](classOf[Generic], "org.freedesktop.NetworkManager.Device.Generic") {

  object Props {
    val HwAddress = read[java.lang.String]("HwAddress")
    val TypeDescription = read[java.lang.String]("TypeDescription")
  }

  object Instances {

    object `org.freedesktop.NetworkManager` extends BusInstances("org.freedesktop.NetworkManager") {
      val `/org/freedesktop/NetworkManager/Devices/2` = instance("/org/freedesktop/NetworkManager/Devices/2")
    }
                 
  }

}
           