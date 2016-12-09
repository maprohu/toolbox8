package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.AgentManager1")
trait AgentManager1 extends org.freedesktop.dbus.DBusInterface {

  def RegisterAgent(
    agent: org.freedesktop.dbus.DBusInterface,
    capability: java.lang.String
  ) : scala.Unit
               
  def UnregisterAgent(agent: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
  def RequestDefaultAgent(agent: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
}
           