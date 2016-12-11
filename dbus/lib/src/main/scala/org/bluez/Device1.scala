package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.Device1")
trait Device1 extends org.freedesktop.dbus.DBusInterface {

  def CancelPairing() : scala.Unit
               
  def Connect() : scala.Unit
               
  def ConnectProfile(UUID: java.lang.String) : scala.Unit
               
  def Disconnect() : scala.Unit
               
  def DisconnectProfile(UUID: java.lang.String) : scala.Unit
               
  def Pair() : scala.Unit
               
}

object Device1 extends toolbox8.dbus.common.DBInterfaceCompanion[Device1](classOf[Device1], "org.bluez.Device1") {

  object Props {
    val Adapter = read[org.freedesktop.dbus.DBusInterface]("Adapter")
    val Address = read[java.lang.String]("Address")
    val Alias = readwrite[java.lang.String]("Alias")
    val Appearance = read[org.freedesktop.dbus.UInt16]("Appearance")
    val Blocked = readwrite[java.lang.Boolean]("Blocked")
    val Class = read[org.freedesktop.dbus.UInt32]("Class")
    val Connected = read[java.lang.Boolean]("Connected")
    val Icon = read[java.lang.String]("Icon")
    val LegacyPairing = read[java.lang.Boolean]("LegacyPairing")
    val Modalias = read[java.lang.String]("Modalias")
    val Name = read[java.lang.String]("Name")
    val Paired = read[java.lang.Boolean]("Paired")
    val RSSI = read[java.lang.Short]("RSSI")
    val Trusted = readwrite[java.lang.Boolean]("Trusted")
    val UUIDs = read[java.util.List[java.lang.String]]("UUIDs")
  }

  object Instances {

    object `org.bluez` extends BusInstances("org.bluez") {
      val `/org/bluez/hci0/dev_0C_E0_E4_F2_34_C0` = instance("/org/bluez/hci0/dev_0C_E0_E4_F2_34_C0")
      val `/org/bluez/hci0/dev_B8_27_EB_C9_F1_08` = instance("/org/bluez/hci0/dev_B8_27_EB_C9_F1_08")
    }
                 
  }

}
           