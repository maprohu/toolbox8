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
      val `/org/freedesktop/NetworkManager/ActiveConnection/1` = instance("/org/freedesktop/NetworkManager/ActiveConnection/1")
      val `/org/freedesktop/NetworkManager/ActiveConnection/0` = instance("/org/freedesktop/NetworkManager/ActiveConnection/0")
      val `/org/freedesktop/NetworkManager/AccessPoint/7` = instance("/org/freedesktop/NetworkManager/AccessPoint/7")
      val `/org/freedesktop/NetworkManager/AccessPoint/6` = instance("/org/freedesktop/NetworkManager/AccessPoint/6")
      val `/org/freedesktop/NetworkManager/AccessPoint/24` = instance("/org/freedesktop/NetworkManager/AccessPoint/24")
      val `/org/freedesktop/NetworkManager/AccessPoint/23` = instance("/org/freedesktop/NetworkManager/AccessPoint/23")
      val `/org/freedesktop/NetworkManager/AccessPoint/9` = instance("/org/freedesktop/NetworkManager/AccessPoint/9")
      val `/org/freedesktop/NetworkManager/AccessPoint/22` = instance("/org/freedesktop/NetworkManager/AccessPoint/22")
      val `/org/freedesktop/NetworkManager/AccessPoint/21` = instance("/org/freedesktop/NetworkManager/AccessPoint/21")
      val `/org/freedesktop/NetworkManager/AccessPoint/20` = instance("/org/freedesktop/NetworkManager/AccessPoint/20")
      val `/org/freedesktop/NetworkManager/AccessPoint/5` = instance("/org/freedesktop/NetworkManager/AccessPoint/5")
      val `/org/freedesktop/NetworkManager/AccessPoint/4` = instance("/org/freedesktop/NetworkManager/AccessPoint/4")
      val `/org/freedesktop/NetworkManager/AccessPoint/3` = instance("/org/freedesktop/NetworkManager/AccessPoint/3")
      val `/org/freedesktop/NetworkManager/AccessPoint/2` = instance("/org/freedesktop/NetworkManager/AccessPoint/2")
      val `/org/freedesktop/NetworkManager/AccessPoint/1` = instance("/org/freedesktop/NetworkManager/AccessPoint/1")
      val `/org/freedesktop/NetworkManager/AccessPoint/0` = instance("/org/freedesktop/NetworkManager/AccessPoint/0")
      val `/org/freedesktop/NetworkManager/AccessPoint/37` = instance("/org/freedesktop/NetworkManager/AccessPoint/37")
      val `/org/freedesktop/NetworkManager/AccessPoint/59` = instance("/org/freedesktop/NetworkManager/AccessPoint/59")
      val `/org/freedesktop/NetworkManager/AccessPoint/58` = instance("/org/freedesktop/NetworkManager/AccessPoint/58")
      val `/org/freedesktop/NetworkManager/AccessPoint/55` = instance("/org/freedesktop/NetworkManager/AccessPoint/55")
      val `/org/freedesktop/NetworkManager/AccessPoint/54` = instance("/org/freedesktop/NetworkManager/AccessPoint/54")
      val `/org/freedesktop/NetworkManager/AccessPoint/32` = instance("/org/freedesktop/NetworkManager/AccessPoint/32")
      val `/org/freedesktop/NetworkManager/AccessPoint/52` = instance("/org/freedesktop/NetworkManager/AccessPoint/52")
      val `/org/freedesktop/NetworkManager/AccessPoint/50` = instance("/org/freedesktop/NetworkManager/AccessPoint/50")
      val `/org/freedesktop/NetworkManager/AccessPoint/18` = instance("/org/freedesktop/NetworkManager/AccessPoint/18")
      val `/org/freedesktop/NetworkManager/AccessPoint/17` = instance("/org/freedesktop/NetworkManager/AccessPoint/17")
      val `/org/freedesktop/NetworkManager/AccessPoint/16` = instance("/org/freedesktop/NetworkManager/AccessPoint/16")
      val `/org/freedesktop/NetworkManager/AccessPoint/15` = instance("/org/freedesktop/NetworkManager/AccessPoint/15")
      val `/org/freedesktop/NetworkManager/AccessPoint/14` = instance("/org/freedesktop/NetworkManager/AccessPoint/14")
      val `/org/freedesktop/NetworkManager/AccessPoint/13` = instance("/org/freedesktop/NetworkManager/AccessPoint/13")
      val `/org/freedesktop/NetworkManager/AccessPoint/49` = instance("/org/freedesktop/NetworkManager/AccessPoint/49")
      val `/org/freedesktop/NetworkManager/AccessPoint/48` = instance("/org/freedesktop/NetworkManager/AccessPoint/48")
      val `/org/freedesktop/NetworkManager/AccessPoint/43` = instance("/org/freedesktop/NetworkManager/AccessPoint/43")
      val `/org/freedesktop/NetworkManager/AccessPoint/26` = instance("/org/freedesktop/NetworkManager/AccessPoint/26")
      val `/org/freedesktop/NetworkManager/AccessPoint/41` = instance("/org/freedesktop/NetworkManager/AccessPoint/41")
      val `/org/freedesktop/NetworkManager/AccessPoint/40` = instance("/org/freedesktop/NetworkManager/AccessPoint/40")
      val `/org/freedesktop/NetworkManager/Devices/3` = instance("/org/freedesktop/NetworkManager/Devices/3")
      val `/org/freedesktop/NetworkManager/Devices/2` = instance("/org/freedesktop/NetworkManager/Devices/2")
      val `/org/freedesktop/NetworkManager/Devices/1` = instance("/org/freedesktop/NetworkManager/Devices/1")
      val `/org/freedesktop/NetworkManager/Devices/0` = instance("/org/freedesktop/NetworkManager/Devices/0")
      val `/org/freedesktop/NetworkManager/Devices/4` = instance("/org/freedesktop/NetworkManager/Devices/4")
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
      val `/org/freedesktop/NetworkManager/Settings/9` = instance("/org/freedesktop/NetworkManager/Settings/9")
      val `/org/freedesktop/NetworkManager/IP6Config/3` = instance("/org/freedesktop/NetworkManager/IP6Config/3")
      val `/org/freedesktop/NetworkManager/IP6Config/2` = instance("/org/freedesktop/NetworkManager/IP6Config/2")
      val `/org/freedesktop/NetworkManager/IP6Config/5` = instance("/org/freedesktop/NetworkManager/IP6Config/5")
      val `/org/freedesktop/NetworkManager/IP6Config/4` = instance("/org/freedesktop/NetworkManager/IP6Config/4")
      val `/org/freedesktop/NetworkManager/IP4Config/3` = instance("/org/freedesktop/NetworkManager/IP4Config/3")
      val `/org/freedesktop/NetworkManager/IP4Config/2` = instance("/org/freedesktop/NetworkManager/IP4Config/2")
      val `/org/freedesktop/NetworkManager/IP4Config/5` = instance("/org/freedesktop/NetworkManager/IP4Config/5")
      val `/org/freedesktop/NetworkManager/IP4Config/4` = instance("/org/freedesktop/NetworkManager/IP4Config/4")
    }
                 
  }

}
           