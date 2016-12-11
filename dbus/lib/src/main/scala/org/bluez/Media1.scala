package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.Media1")
trait Media1 extends org.freedesktop.dbus.DBusInterface {

  def RegisterEndpoint(
    endpoint: org.freedesktop.dbus.DBusInterface,
    properties: java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]
  ) : scala.Unit
               
  def RegisterPlayer(
    player: org.freedesktop.dbus.DBusInterface,
    properties: java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]
  ) : scala.Unit
               
  def UnregisterEndpoint(endpoint: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
  def UnregisterPlayer(player: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
}

object Media1 extends toolbox8.dbus.common.DBInterfaceCompanion[Media1](classOf[Media1], "org.bluez.Media1") {

  object Props {

  }

  object Instances {

    object `org.bluez` extends BusInstances("org.bluez") {
      val `/org/bluez/hci0` = instance("/org/bluez/hci0")
    }
                 
  }

}
           