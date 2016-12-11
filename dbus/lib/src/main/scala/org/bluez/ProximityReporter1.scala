package org.bluez

@org.freedesktop.dbus.DBusInterfaceName("org.bluez.ProximityReporter1")
trait ProximityReporter1 extends org.freedesktop.dbus.DBusInterface {


}

object ProximityReporter1 extends toolbox8.dbus.common.DBInterfaceCompanion[ProximityReporter1](classOf[ProximityReporter1], "org.bluez.ProximityReporter1") {

  object Props {
    val ImmediateAlertLevel = read[java.lang.String]("ImmediateAlertLevel")
    val LinkLossAlertLevel = read[java.lang.String]("LinkLossAlertLevel")
  }

  object Instances {

    object `org.bluez` extends BusInstances("org.bluez") {
      val `/org/bluez/hci0/dev_B8_27_EB_C9_F1_08` = instance("/org/bluez/hci0/dev_B8_27_EB_C9_F1_08")
      val `/org/bluez/hci0/dev_FF_FF_C0_00_42_90` = instance("/org/bluez/hci0/dev_FF_FF_C0_00_42_90")
    }
                 
  }

}
           