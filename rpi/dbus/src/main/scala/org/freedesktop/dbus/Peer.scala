package org.freedesktop.dbus

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.DBus.Peer")
trait Peer extends org.freedesktop.dbus.DBusInterface {

  def GetMachineId() : java.lang.String
               
  def Ping() : scala.Unit
               
}

object Peer extends toolbox8.rpi.dbus.DBInterfaceCompanion[Peer](classOf[Peer], "org.freedesktop.DBus.Peer") {

  object Props {

  }

  object Instances {

    object `org.freedesktop.NetworkManager` extends BusInstances("org.freedesktop.NetworkManager") {
      val `/org/freedesktop` = instance("/org/freedesktop")
      val `/org/freedesktop/NetworkManager` = instance("/org/freedesktop/NetworkManager")
      val `/org/freedesktop/NetworkManager/DHCP4Config/0` = instance("/org/freedesktop/NetworkManager/DHCP4Config/0")
      val `/org/freedesktop/NetworkManager/ActiveConnection/1` = instance("/org/freedesktop/NetworkManager/ActiveConnection/1")
      val `/org/freedesktop/NetworkManager/ActiveConnection/0` = instance("/org/freedesktop/NetworkManager/ActiveConnection/0")
      val `/org/freedesktop/NetworkManager/AccessPoint/24` = instance("/org/freedesktop/NetworkManager/AccessPoint/24")
      val `/org/freedesktop/NetworkManager/AccessPoint/138` = instance("/org/freedesktop/NetworkManager/AccessPoint/138")
      val `/org/freedesktop/NetworkManager/AccessPoint/9` = instance("/org/freedesktop/NetworkManager/AccessPoint/9")
      val `/org/freedesktop/NetworkManager/AccessPoint/22` = instance("/org/freedesktop/NetworkManager/AccessPoint/22")
      val `/org/freedesktop/NetworkManager/AccessPoint/6` = instance("/org/freedesktop/NetworkManager/AccessPoint/6")
      val `/org/freedesktop/NetworkManager/AccessPoint/133` = instance("/org/freedesktop/NetworkManager/AccessPoint/133")
      val `/org/freedesktop/NetworkManager/AccessPoint/3` = instance("/org/freedesktop/NetworkManager/AccessPoint/3")
      val `/org/freedesktop/NetworkManager/AccessPoint/1` = instance("/org/freedesktop/NetworkManager/AccessPoint/1")
      val `/org/freedesktop/NetworkManager/AccessPoint/0` = instance("/org/freedesktop/NetworkManager/AccessPoint/0")
      val `/org/freedesktop/NetworkManager/AccessPoint/187` = instance("/org/freedesktop/NetworkManager/AccessPoint/187")
      val `/org/freedesktop/NetworkManager/AccessPoint/186` = instance("/org/freedesktop/NetworkManager/AccessPoint/186")
      val `/org/freedesktop/NetworkManager/AccessPoint/185` = instance("/org/freedesktop/NetworkManager/AccessPoint/185")
      val `/org/freedesktop/NetworkManager/AccessPoint/180` = instance("/org/freedesktop/NetworkManager/AccessPoint/180")
      val `/org/freedesktop/NetworkManager/AccessPoint/148` = instance("/org/freedesktop/NetworkManager/AccessPoint/148")
      val `/org/freedesktop/NetworkManager/AccessPoint/19` = instance("/org/freedesktop/NetworkManager/AccessPoint/19")
      val `/org/freedesktop/NetworkManager/AccessPoint/30` = instance("/org/freedesktop/NetworkManager/AccessPoint/30")
      val `/org/freedesktop/NetworkManager/AccessPoint/126` = instance("/org/freedesktop/NetworkManager/AccessPoint/126")
      val `/org/freedesktop/NetworkManager/AccessPoint/11` = instance("/org/freedesktop/NetworkManager/AccessPoint/11")
      val `/org/freedesktop/NetworkManager/AccessPoint/83` = instance("/org/freedesktop/NetworkManager/AccessPoint/83")
      val `/org/freedesktop/NetworkManager/AccessPoint/199` = instance("/org/freedesktop/NetworkManager/AccessPoint/199")
      val `/org/freedesktop/NetworkManager/AccessPoint/208` = instance("/org/freedesktop/NetworkManager/AccessPoint/208")
      val `/org/freedesktop/NetworkManager/AccessPoint/197` = instance("/org/freedesktop/NetworkManager/AccessPoint/197")
      val `/org/freedesktop/NetworkManager/AccessPoint/207` = instance("/org/freedesktop/NetworkManager/AccessPoint/207")
      val `/org/freedesktop/NetworkManager/AccessPoint/206` = instance("/org/freedesktop/NetworkManager/AccessPoint/206")
      val `/org/freedesktop/NetworkManager/AccessPoint/205` = instance("/org/freedesktop/NetworkManager/AccessPoint/205")
      val `/org/freedesktop/NetworkManager/AccessPoint/204` = instance("/org/freedesktop/NetworkManager/AccessPoint/204")
      val `/org/freedesktop/NetworkManager/AccessPoint/177` = instance("/org/freedesktop/NetworkManager/AccessPoint/177")
      val `/org/freedesktop/NetworkManager/AccessPoint/191` = instance("/org/freedesktop/NetworkManager/AccessPoint/191")
      val `/org/freedesktop/NetworkManager/AccessPoint/201` = instance("/org/freedesktop/NetworkManager/AccessPoint/201")
      val `/org/freedesktop/NetworkManager/AccessPoint/173` = instance("/org/freedesktop/NetworkManager/AccessPoint/173")
      val `/org/freedesktop/NetworkManager/AccessPoint/8` = instance("/org/freedesktop/NetworkManager/AccessPoint/8")
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
           