package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.HealthManager1")
trait HealthManager1 extends org.freedesktop.dbus.DBusInterface {

  def CreateApplication(config: java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]) : org.freedesktop.dbus.DBusInterface
               
  def DestroyApplication(application: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
}

object HealthManager1 extends toolbox8.rpi.dbus.DBInterfaceCompanion[HealthManager1](classOf[HealthManager1], "org.bluez.HealthManager1") {

  object Props {

  }

  object Instances {

    object `org.bluez` extends BusInstances("org.bluez") {
      val `/org/bluez` = instance("/org/bluez")
    }
                 
  }

}
           