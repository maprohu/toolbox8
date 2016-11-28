package toolbox8.jartree.request

import java.io._
import java.net.Socket

import toolbox8.jartree.common.JarTreeApp.Config
import toolbox8.jartree.requestapi.RequestMarker
import toolbox8.jartree.streamapp._

/**
  * Created by maprohu on 21-11-2016.
  */
object StreamAppRequest {



  case class StreamAppConnection(
    socket: Socket,
    os: OutputStream,
    dos: ObjectOutputStream,
    is: InputStream,
    dis: ObjectInputStream
  ) {
    def close() = {
      dos.close()
      os.close()
      dis.close()
      is.close()
      socket.close()
    }
  }


  def open(
    target: Config
  ) = {

    val socket = new Socket(
      target.host,
      target.servicePort
    )

    val os = socket.getOutputStream
    val dos = new ObjectOutputStream(os)
    val is = socket.getInputStream
    val dis = new ObjectInputStream(is)

    StreamAppConnection(
      socket,
      os,
      dos,
      is,
      dis
    )
  }


  def request[In, Out](
    marker: RequestMarker[In, Out],
    inputParam: In,
    target: Config
  ) : Out = {
    val c = open(target)
    try {
      import c._

      val preq =
        RunMarked(
          Vector.empty
        )
      println(preq)
      dos.writeObject(preq)
      dos.writeObject(RunMarkedRequest(marker, inputParam))
      dos.flush()

      val result =
        dis
          .readObject()
          .asInstanceOf[Out]

      result
    } finally {
      c.close()
    }

  }

}
