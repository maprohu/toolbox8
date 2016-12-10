package org.freedesktop.networkmanager.settings

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.Settings.Connection")
trait Connection extends org.freedesktop.dbus.DBusInterface {

  def ClearSecrets() : scala.Unit
               
  def Delete() : scala.Unit
               
  def GetSecrets(setting_name: java.lang.String) : java.util.Map[java.lang.String, java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]
               
  def GetSettings() : java.util.Map[java.lang.String, java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]
               
  def Save() : scala.Unit
               
  def Update(properties: java.util.Map[java.lang.String, java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]) : scala.Unit
               
  def UpdateUnsaved(properties: java.util.Map[java.lang.String, java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]) : scala.Unit
               
}

object Connection extends toolbox8.rpi.dbus.DBInterfaceCompanion[Connection](classOf[Connection], "org.freedesktop.NetworkManager.Settings.Connection") {

  object Props {
    val Unsaved = read[java.lang.Boolean]("Unsaved")
  }

  object Instances {

    object `org.freedesktop.NetworkManager` extends BusInstances("org.freedesktop.NetworkManager") {
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
    }
                 
  }

}
           