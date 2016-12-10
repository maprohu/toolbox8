package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.MediaControl1")
trait MediaControl1 extends org.freedesktop.dbus.DBusInterface {

  def FastForward() : scala.Unit
               
  def Next() : scala.Unit
               
  def Pause() : scala.Unit
               
  def Play() : scala.Unit
               
  def Previous() : scala.Unit
               
  def Rewind() : scala.Unit
               
  def Stop() : scala.Unit
               
  def VolumeDown() : scala.Unit
               
  def VolumeUp() : scala.Unit
               
}

object MediaControl1 extends toolbox8.rpi.dbus.DBInterfaceCompanion[MediaControl1](classOf[MediaControl1], "org.bluez.MediaControl1") {

  object Props {
    val Connected = read[java.lang.Boolean]("Connected")
    val Player = read[org.freedesktop.dbus.DBusInterface]("Player")
  }

  object Instances {

    object `org.bluez` extends BusInstances("org.bluez") {
      val `/org/bluez/hci0/dev_0C_E0_E4_F2_34_C0` = instance("/org/bluez/hci0/dev_0C_E0_E4_F2_34_C0")
      val `/org/bluez/hci0/dev_B8_27_EB_C9_F1_08` = instance("/org/bluez/hci0/dev_B8_27_EB_C9_F1_08")
    }
                 
  }

}
           