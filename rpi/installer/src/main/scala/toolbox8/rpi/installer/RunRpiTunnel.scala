package toolbox8.rpi.installer

/**
  * Created by maprohu on 20-11-2016.
  */
object RunRpiTunnel {

  //  val Target = Rpis.Home
  val Target = Rpis.MobileCable


  def main(args: Array[String]): Unit = {
    RpiService
      .tunnel()(
        Target
      )

  }

}
