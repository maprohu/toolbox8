package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.Network1")
trait Network1 extends org.freedesktop.dbus.DBusInterface {

  def Connect(uuid: java.lang.String) : java.lang.String
               
  def Disconnect() : scala.Unit
               
}

object Network1 extends toolbox8.dbus.common.DBInterfaceCompanion[Network1](classOf[Network1], "org.bluez.Network1") {

  object Props {
    val Connected = read[java.lang.Boolean]("Connected")
    val Interface = read[java.lang.String]("Interface")
    val UUID = read[java.lang.String]("UUID")
  }

  object Instances {

    object `org.bluez` extends BusInstances("org.bluez") {
      val `/org/bluez/hci0/dev_B8_27_EB_C9_F1_08` = instance("/org/bluez/hci0/dev_B8_27_EB_C9_F1_08")
    }
                 
  }

}
           