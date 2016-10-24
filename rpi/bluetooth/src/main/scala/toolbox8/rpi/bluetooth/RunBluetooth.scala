package toolbox8.rpi.bluetooth

import javax.bluetooth._

import scala.io.StdIn

/**
  * Created by maprohu on 24-10-2016.
  */
object RunBluetooth {

  def main(args: Array[String]): Unit = {

    val listener = new DiscoveryListener {
      override def deviceDiscovered(btDevice: RemoteDevice, cod: DeviceClass): Unit = ???
      override def inquiryCompleted(discType: Int): Unit = {
        println(s"completed: ${discType}")
      }
      override def servicesDiscovered(transID: Int, servRecord: Array[ServiceRecord]): Unit = ???
      override def serviceSearchCompleted(transID: Int, respCode: Int): Unit = ???
    }

    val started = LocalDevice
      .getLocalDevice
      .getDiscoveryAgent()
      .startInquiry(
        DiscoveryAgent.GIAC,
        listener
      )

    println(started)

//    val ds = LocalDevice
//      .getLocalDevice
//      .getDiscoveryAgent
//      .retrieveDevices(
//        DiscoveryAgent.CACHED
//      )
//
//    println(ds.toSeq)

    StdIn.readLine()

  }

}
