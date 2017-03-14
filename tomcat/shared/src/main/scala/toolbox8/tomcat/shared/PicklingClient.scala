package toolbox8.tomcat.shared

import java.nio.ByteBuffer
import boopickle.Default._

/**
  * Created by pappmar on 14/03/2017.
  */
object PicklingClient {

  def unpickle(bb: ByteBuffer) : ServerToClient = {
    Unpickle[ServerToClient].fromBytes(bb)
  }

  def pickle(c2s: ClientToServer) : ByteBuffer = {
    Pickle(c2s).toByteBuffer
  }

}
