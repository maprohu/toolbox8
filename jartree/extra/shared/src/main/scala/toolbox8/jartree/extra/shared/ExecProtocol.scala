package toolbox8.jartree.extra.shared

import akka.stream.scaladsl.Flow
import akka.util.ByteString

/**
  * Created by maprohu on 01-11-2016.
  */
object ExecProtocol {

  trait Executable {
    def flow : Flow[ByteString, ByteString, _]
  }

  case class Start(


  )

}
