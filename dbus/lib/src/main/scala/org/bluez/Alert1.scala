package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.Alert1")
trait Alert1 extends org.freedesktop.dbus.DBusInterface {

  def NewAlert(
    category: java.lang.String,
    count: org.freedesktop.dbus.UInt16,
    description: java.lang.String
  ) : scala.Unit
               
  def RegisterAlert(
    category: java.lang.String,
    agent: org.freedesktop.dbus.DBusInterface
  ) : scala.Unit
               
  def UnreadAlert(
    category: java.lang.String,
    count: org.freedesktop.dbus.UInt16
  ) : scala.Unit
               
}

object Alert1 extends toolbox8.dbus.common.DBInterfaceCompanion[Alert1](classOf[Alert1], "org.bluez.Alert1") {

  object Props {

  }

  object Instances {

    object `org.bluez` extends BusInstances("org.bluez") {
      val `/org/bluez` = instance("/org/bluez")
    }
                 
  }

}
           