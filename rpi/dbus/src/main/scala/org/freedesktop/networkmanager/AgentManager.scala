package org.freedesktop.networkmanager

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.AgentManager")
trait AgentManager extends org.freedesktop.dbus.DBusInterface {

  def Register(identifier: java.lang.String) : scala.Unit
               
  def RegisterWithCapabilities(
    identifier: java.lang.String,
    capabilities: org.freedesktop.dbus.UInt32
  ) : scala.Unit
               
  def Unregister() : scala.Unit
               
}
           