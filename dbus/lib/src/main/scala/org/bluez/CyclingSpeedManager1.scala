package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.CyclingSpeedManager1")
trait CyclingSpeedManager1 extends org.freedesktop.dbus.DBusInterface {

  def RegisterWatcher(agent: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
  def UnregisterWatcher(agent: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
}

object CyclingSpeedManager1 extends toolbox8.dbus.common.DBInterfaceCompanion[CyclingSpeedManager1](classOf[CyclingSpeedManager1], "org.bluez.CyclingSpeedManager1") {

  object Props {

  }

  object Instances {

    object `org.bluez` extends BusInstances("org.bluez") {
      val `/org/bluez/hci0` = instance("/org/bluez/hci0")
    }
                 
  }

}
           