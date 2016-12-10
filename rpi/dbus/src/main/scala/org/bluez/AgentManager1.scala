package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.AgentManager1")
trait AgentManager1 extends org.freedesktop.dbus.DBusInterface {

  def RegisterAgent(
    agent: org.freedesktop.dbus.DBusInterface,
    capability: java.lang.String
  ) : scala.Unit
               
  def RequestDefaultAgent(agent: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
  def UnregisterAgent(agent: org.freedesktop.dbus.DBusInterface) : scala.Unit
               
}

object AgentManager1 extends toolbox8.rpi.dbus.DBInterfaceCompanion[AgentManager1](classOf[AgentManager1], "org.bluez.AgentManager1") {

  object Props {

  }

  object Instances {

    object `org.bluez` extends BusInstances("org.bluez") {
      val `/org/bluez` = instance("/org/bluez")
    }
                 
  }

}
           