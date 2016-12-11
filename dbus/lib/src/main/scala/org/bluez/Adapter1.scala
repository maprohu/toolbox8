package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.Adapter1")
trait Adapter1 extends org.freedesktop.dbus.DBusInterface {

  def RemoveDevice(device: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
  def StartDiscovery() : scala.Unit
               
  def StopDiscovery() : scala.Unit
               
}

object Adapter1 extends toolbox8.dbus.common.DBInterfaceCompanion[Adapter1](classOf[Adapter1], "org.bluez.Adapter1") {

  object Props {
    val Address = read[java.lang.String]("Address")
    val Alias = readwrite[java.lang.String]("Alias")
    val Class = read[org.freedesktop.dbus.UInt32]("Class")
    val Discoverable = readwrite[java.lang.Boolean]("Discoverable")
    val DiscoverableTimeout = readwrite[org.freedesktop.dbus.UInt32]("DiscoverableTimeout")
    val Discovering = read[java.lang.Boolean]("Discovering")
    val Modalias = read[java.lang.String]("Modalias")
    val Name = read[java.lang.String]("Name")
    val Pairable = readwrite[java.lang.Boolean]("Pairable")
    val PairableTimeout = readwrite[org.freedesktop.dbus.UInt32]("PairableTimeout")
    val Powered = readwrite[java.lang.Boolean]("Powered")
    val UUIDs = read[java.util.List[java.lang.String]]("UUIDs")
  }

  object Instances {

    object `org.bluez` extends BusInstances("org.bluez") {
      val `/org/bluez/hci0` = instance("/org/bluez/hci0")
    }
                 
  }

}
           