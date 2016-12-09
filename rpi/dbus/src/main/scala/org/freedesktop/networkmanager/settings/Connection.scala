package org.freedesktop.networkmanager.settings

@org.freedesktop.dbus.DBusInterfaceName("org.freedesktop.NetworkManager.Settings.Connection")
trait Connection extends org.freedesktop.dbus.DBusInterface {

  def Update(properties: java.util.Map[java.lang.String, java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]) : scala.Unit
               
  def UpdateUnsaved(properties: java.util.Map[java.lang.String, java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]) : scala.Unit
               
  def Delete() : scala.Unit
               
  def GetSettings() : java.util.Map[java.lang.String, java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]
               
  def GetSecrets(setting_name: java.lang.String) : java.util.Map[java.lang.String, java.util.Map[java.lang.String, org.freedesktop.dbus.Variant[_]]]
               
  def ClearSecrets() : scala.Unit
               
  def Save() : scala.Unit
               
}
           