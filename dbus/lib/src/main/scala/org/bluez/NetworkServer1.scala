package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.NetworkServer1")
trait NetworkServer1 extends org.freedesktop.dbus.DBusInterface {

  def Register(
    uuid: java.lang.String,
    bridge: java.lang.String
  ) : scala.Unit
               
  def Unregister(uuid: java.lang.String) : scala.Unit
               
}

object NetworkServer1 extends toolbox8.dbus.common.DBInterfaceCompanion[NetworkServer1](classOf[NetworkServer1], "org.bluez.NetworkServer1") {

  object Props {

  }

  object Instances {

    object `org.bluez` extends BusInstances("org.bluez") {
      val `/org/bluez/hci0` = instance("/org/bluez/hci0")
    }
                 
  }

}
           