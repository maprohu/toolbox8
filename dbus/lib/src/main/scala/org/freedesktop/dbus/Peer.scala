package org.freedesktop.dbus

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.DBus.Peer")
trait Peer extends org.freedesktop.dbus.DBusInterface {

  def GetMachineId() : java.lang.String
               
  def Ping() : scala.Unit
               
}

object Peer extends toolbox8.dbus.common.DBInterfaceCompanion[Peer](classOf[Peer], "org.freedesktop.DBus.Peer") {

  object Props {

  }

  object Instances {

    object `org.freedesktop.NetworkManager` extends BusInstances("org.freedesktop.NetworkManager") {
      val `/org/freedesktop` = instance("/org/freedesktop")
      val `/org/freedesktop/NetworkManager` = instance("/org/freedesktop/NetworkManager")
      val `/org/freedesktop/NetworkManager/DHCP4Config/0` = instance("/org/freedesktop/NetworkManager/DHCP4Config/0")
      val `/org/freedesktop/NetworkManager/ActiveConnection/0` = instance("/org/freedesktop/NetworkManager/ActiveConnection/0")
      val `/org/freedesktop/NetworkManager/AccessPoint/3` = instance("/org/freedesktop/NetworkManager/AccessPoint/3")
      val `/org/freedesktop/NetworkManager/AccessPoint/77` = instance("/org/freedesktop/NetworkManager/AccessPoint/77")
      val `/org/freedesktop/NetworkManager/AccessPoint/1` = instance("/org/freedesktop/NetworkManager/AccessPoint/1")
      val `/org/freedesktop/NetworkManager/AccessPoint/0` = instance("/org/freedesktop/NetworkManager/AccessPoint/0")
      val `/org/freedesktop/NetworkManager/AccessPoint/2` = instance("/org/freedesktop/NetworkManager/AccessPoint/2")
      val `/org/freedesktop/NetworkManager/AccessPoint/98` = instance("/org/freedesktop/NetworkManager/AccessPoint/98")
      val `/org/freedesktop/NetworkManager/AccessPoint/106` = instance("/org/freedesktop/NetworkManager/AccessPoint/106")
      val `/org/freedesktop/NetworkManager/AccessPoint/112` = instance("/org/freedesktop/NetworkManager/AccessPoint/112")
      val `/org/freedesktop/NetworkManager/AccessPoint/96` = instance("/org/freedesktop/NetworkManager/AccessPoint/96")
      val `/org/freedesktop/NetworkManager/AccessPoint/111` = instance("/org/freedesktop/NetworkManager/AccessPoint/111")
      val `/org/freedesktop/NetworkManager/AccessPoint/102` = instance("/org/freedesktop/NetworkManager/AccessPoint/102")
      val `/org/freedesktop/NetworkManager/AccessPoint/101` = instance("/org/freedesktop/NetworkManager/AccessPoint/101")
      val `/org/freedesktop/NetworkManager/AccessPoint/67` = instance("/org/freedesktop/NetworkManager/AccessPoint/67")
      val `/org/freedesktop/NetworkManager/AccessPoint/91` = instance("/org/freedesktop/NetworkManager/AccessPoint/91")
      val `/org/freedesktop/NetworkManager/AccessPoint/46` = instance("/org/freedesktop/NetworkManager/AccessPoint/46")
      val `/org/freedesktop/NetworkManager/AccessPoint/37` = instance("/org/freedesktop/NetworkManager/AccessPoint/37")
      val `/org/freedesktop/NetworkManager/AccessPoint/36` = instance("/org/freedesktop/NetworkManager/AccessPoint/36")
      val `/org/freedesktop/NetworkManager/AccessPoint/27` = instance("/org/freedesktop/NetworkManager/AccessPoint/27")
      val `/org/freedesktop/NetworkManager/AccessPoint/19` = instance("/org/freedesktop/NetworkManager/AccessPoint/19")
      val `/org/freedesktop/NetworkManager/AccessPoint/83` = instance("/org/freedesktop/NetworkManager/AccessPoint/83")
      val `/org/freedesktop/NetworkManager/AccessPoint/22` = instance("/org/freedesktop/NetworkManager/AccessPoint/22")
      val `/org/freedesktop/NetworkManager/AccessPoint/16` = instance("/org/freedesktop/NetworkManager/AccessPoint/16")
      val `/org/freedesktop/NetworkManager/AccessPoint/20` = instance("/org/freedesktop/NetworkManager/AccessPoint/20")
      val `/org/freedesktop/NetworkManager/AccessPoint/21` = instance("/org/freedesktop/NetworkManager/AccessPoint/21")
      val `/org/freedesktop/NetworkManager/AccessPoint/13` = instance("/org/freedesktop/NetworkManager/AccessPoint/13")
      val `/org/freedesktop/NetworkManager/AccessPoint/12` = instance("/org/freedesktop/NetworkManager/AccessPoint/12")
      val `/org/freedesktop/NetworkManager/AccessPoint/8` = instance("/org/freedesktop/NetworkManager/AccessPoint/8")
      val `/org/freedesktop/NetworkManager/AccessPoint/5` = instance("/org/freedesktop/NetworkManager/AccessPoint/5")
      val `/org/freedesktop/NetworkManager/AccessPoint/7` = instance("/org/freedesktop/NetworkManager/AccessPoint/7")
      val `/org/freedesktop/NetworkManager/Devices/2` = instance("/org/freedesktop/NetworkManager/Devices/2")
      val `/org/freedesktop/NetworkManager/Devices/1` = instance("/org/freedesktop/NetworkManager/Devices/1")
      val `/org/freedesktop/NetworkManager/Devices/0` = instance("/org/freedesktop/NetworkManager/Devices/0")
      val `/org/freedesktop/NetworkManager/AgentManager` = instance("/org/freedesktop/NetworkManager/AgentManager")
      val `/org/freedesktop/NetworkManager/Settings` = instance("/org/freedesktop/NetworkManager/Settings")
      val `/org/freedesktop/NetworkManager/Settings/8` = instance("/org/freedesktop/NetworkManager/Settings/8")
      val `/org/freedesktop/NetworkManager/Settings/7` = instance("/org/freedesktop/NetworkManager/Settings/7")
      val `/org/freedesktop/NetworkManager/Settings/6` = instance("/org/freedesktop/NetworkManager/Settings/6")
      val `/org/freedesktop/NetworkManager/Settings/5` = instance("/org/freedesktop/NetworkManager/Settings/5")
      val `/org/freedesktop/NetworkManager/Settings/4` = instance("/org/freedesktop/NetworkManager/Settings/4")
      val `/org/freedesktop/NetworkManager/Settings/3` = instance("/org/freedesktop/NetworkManager/Settings/3")
      val `/org/freedesktop/NetworkManager/Settings/2` = instance("/org/freedesktop/NetworkManager/Settings/2")
      val `/org/freedesktop/NetworkManager/Settings/1` = instance("/org/freedesktop/NetworkManager/Settings/1")
      val `/org/freedesktop/NetworkManager/Settings/0` = instance("/org/freedesktop/NetworkManager/Settings/0")
      val `/org/freedesktop/NetworkManager/IP6Config/3` = instance("/org/freedesktop/NetworkManager/IP6Config/3")
      val `/org/freedesktop/NetworkManager/IP6Config/2` = instance("/org/freedesktop/NetworkManager/IP6Config/2")
      val `/org/freedesktop/NetworkManager/IP6Config/4` = instance("/org/freedesktop/NetworkManager/IP6Config/4")
      val `/org/freedesktop/NetworkManager/IP4Config/3` = instance("/org/freedesktop/NetworkManager/IP4Config/3")
      val `/org/freedesktop/NetworkManager/IP4Config/2` = instance("/org/freedesktop/NetworkManager/IP4Config/2")
      val `/org/freedesktop/NetworkManager/IP4Config/4` = instance("/org/freedesktop/NetworkManager/IP4Config/4")
    }
                 
  }

}
           