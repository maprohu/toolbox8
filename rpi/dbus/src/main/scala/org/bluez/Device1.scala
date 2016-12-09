package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.Device1")
trait Device1 extends org.freedesktop.dbus.DBusInterface {

  def Disconnect() : scala.Unit
               
  def Connect() : scala.Unit
               
  def ConnectProfile(UUID: java.lang.String) : scala.Unit
               
  def DisconnectProfile(UUID: java.lang.String) : scala.Unit
               
  def Pair() : scala.Unit
               
  def CancelPairing() : scala.Unit
               
}
           