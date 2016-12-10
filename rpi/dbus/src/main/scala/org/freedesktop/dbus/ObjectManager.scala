package org.freedesktop.dbus

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.DBus.ObjectManager")
trait ObjectManager extends org.freedesktop.dbus.DBusInterface {

  def GetManagedObjects() : java.util.Map[org.freedesktop.dbus.DBusInterface, java.util.Map[java.lang.String, java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]]
               
}

object ObjectManager extends toolbox8.rpi.dbus.DBInterfaceCompanion[ObjectManager](classOf[ObjectManager], "org.freedesktop.DBus.ObjectManager") {

  object Props {

  }

  object Instances {

    object `org.bluez` extends BusInstances("org.bluez") {
      val `/` = instance("/")
    }
                 
    object `org.freedesktop.NetworkManager` extends BusInstances("org.freedesktop.NetworkManager") {
      val `/org/freedesktop` = instance("/org/freedesktop")
    }
                 
  }

}
           