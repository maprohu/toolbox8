package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.MediaControl1")
trait MediaControl1 extends org.freedesktop.dbus.DBusInterface {

  def Play() : scala.Unit
               
  def Pause() : scala.Unit
               
  def Stop() : scala.Unit
               
  def Next() : scala.Unit
               
  def Previous() : scala.Unit
               
  def VolumeUp() : scala.Unit
               
  def VolumeDown() : scala.Unit
               
  def FastForward() : scala.Unit
               
  def Rewind() : scala.Unit
               
}
           