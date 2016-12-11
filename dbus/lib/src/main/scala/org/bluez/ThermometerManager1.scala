package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.ThermometerManager1")
trait ThermometerManager1 extends org.freedesktop.dbus.DBusInterface {

  def DisableIntermediateMeasurement(agent: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
  def EnableIntermediateMeasurement(agent: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
  def RegisterWatcher(agent: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
  def UnregisterWatcher(agent: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
}

object ThermometerManager1 extends toolbox8.dbus.common.DBInterfaceCompanion[ThermometerManager1](classOf[ThermometerManager1], "org.bluez.ThermometerManager1") {

  object Props {

  }

  object Instances {

    object `org.bluez` extends BusInstances("org.bluez") {
      val `/org/bluez/hci0` = instance("/org/bluez/hci0")
    }
                 
  }

}
           