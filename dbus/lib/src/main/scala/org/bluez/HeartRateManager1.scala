package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.HeartRateManager1")
trait HeartRateManager1 extends org.freedesktop.dbus.DBusInterface {

  def RegisterWatcher(agent: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
  def UnregisterWatcher(agent: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
}

object HeartRateManager1 extends toolbox8.dbus.common.DBInterfaceCompanion[HeartRateManager1](classOf[HeartRateManager1], "org.bluez.HeartRateManager1") {

  object Props {

  }

  object Instances {

    object `org.bluez` extends BusInstances("org.bluez") {
      val `/org/bluez/hci0` = instance("/org/bluez/hci0")
    }
                 
  }

}
           