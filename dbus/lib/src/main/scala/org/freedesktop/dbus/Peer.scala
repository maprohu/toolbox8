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
      val `/org/freedesktop/NetworkManager/AccessPoint/8` = instance("/org/freedesktop/NetworkManager/AccessPoint/8")
      val `/org/freedesktop/NetworkManager/AccessPoint/7` = instance("/org/freedesktop/NetworkManager/AccessPoint/7")
      val `/org/freedesktop/NetworkManager/AccessPoint/6` = instance("/org/freedesktop/NetworkManager/AccessPoint/6")
      val `/org/freedesktop/NetworkManager/AccessPoint/5` = instance("/org/freedesktop/NetworkManager/AccessPoint/5")
      val `/org/freedesktop/NetworkManager/AccessPoint/4` = instance("/org/freedesktop/NetworkManager/AccessPoint/4")
      val `/org/freedesktop/NetworkManager/AccessPoint/2` = instance("/org/freedesktop/NetworkManager/AccessPoint/2")
      val `/org/freedesktop/NetworkManager/AccessPoint/71` = instance("/org/freedesktop/NetworkManager/AccessPoint/71")
      val `/org/freedesktop/NetworkManager/AccessPoint/1` = instance("/org/freedesktop/NetworkManager/AccessPoint/1")
      val `/org/freedesktop/NetworkManager/AccessPoint/0` = instance("/org/freedesktop/NetworkManager/AccessPoint/0")
      val `/org/freedesktop/NetworkManager/AccessPoint/64` = instance("/org/freedesktop/NetworkManager/AccessPoint/64")
      val `/org/freedesktop/NetworkManager/AccessPoint/69` = instance("/org/freedesktop/NetworkManager/AccessPoint/69")
      val `/org/freedesktop/NetworkManager/AccessPoint/46` = instance("/org/freedesktop/NetworkManager/AccessPoint/46")
      val `/org/freedesktop/NetworkManager/AccessPoint/61` = instance("/org/freedesktop/NetworkManager/AccessPoint/61")
      val `/org/freedesktop/NetworkManager/AccessPoint/58` = instance("/org/freedesktop/NetworkManager/AccessPoint/58")
      val `/org/freedesktop/NetworkManager/AccessPoint/65` = instance("/org/freedesktop/NetworkManager/AccessPoint/65")
      val `/org/freedesktop/NetworkManager/AccessPoint/56` = instance("/org/freedesktop/NetworkManager/AccessPoint/56")
      val `/org/freedesktop/NetworkManager/AccessPoint/70` = instance("/org/freedesktop/NetworkManager/AccessPoint/70")
      val `/org/freedesktop/NetworkManager/AccessPoint/54` = instance("/org/freedesktop/NetworkManager/AccessPoint/54")
      val `/org/freedesktop/NetworkManager/AccessPoint/26` = instance("/org/freedesktop/NetworkManager/AccessPoint/26")
      val `/org/freedesktop/NetworkManager/AccessPoint/25` = instance("/org/freedesktop/NetworkManager/AccessPoint/25")
      val `/org/freedesktop/NetworkManager/AccessPoint/19` = instance("/org/freedesktop/NetworkManager/AccessPoint/19")
      val `/org/freedesktop/NetworkManager/AccessPoint/18` = instance("/org/freedesktop/NetworkManager/AccessPoint/18")
      val `/org/freedesktop/NetworkManager/AccessPoint/17` = instance("/org/freedesktop/NetworkManager/AccessPoint/17")
      val `/org/freedesktop/NetworkManager/AccessPoint/24` = instance("/org/freedesktop/NetworkManager/AccessPoint/24")
      val `/org/freedesktop/NetworkManager/AccessPoint/47` = instance("/org/freedesktop/NetworkManager/AccessPoint/47")
      val `/org/freedesktop/NetworkManager/AccessPoint/21` = instance("/org/freedesktop/NetworkManager/AccessPoint/21")
      val `/org/freedesktop/NetworkManager/AccessPoint/13` = instance("/org/freedesktop/NetworkManager/AccessPoint/13")
      val `/org/freedesktop/NetworkManager/AccessPoint/12` = instance("/org/freedesktop/NetworkManager/AccessPoint/12")
      val `/org/freedesktop/NetworkManager/AccessPoint/10` = instance("/org/freedesktop/NetworkManager/AccessPoint/10")
      val `/org/freedesktop/NetworkManager/AccessPoint/9` = instance("/org/freedesktop/NetworkManager/AccessPoint/9")
      val `/org/freedesktop/NetworkManager/Devices/3` = instance("/org/freedesktop/NetworkManager/Devices/3")
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
           