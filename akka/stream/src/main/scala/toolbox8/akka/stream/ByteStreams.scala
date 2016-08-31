package toolbox8.akka.stream

import akka.stream.scaladsl.Flow
import akka.util.ByteString
import scala.collection.immutable._

/**
  * Created by martonpapp on 31/08/16.
  */


case class SegmentState(

)

object ByteStreams {

  def groupFirstBytes(numBytes: Int) = {
    Flow[ByteString]
      .statefulMapConcat({ () =>
        var buffer = ByteString()
        var left = numBytes

        { bs =>
          if (left == 0) Iterable(bs)
          else if (bs.length < left) {
            buffer = buffer ++ bs
            left -= bs.length
            Iterable()
          } else {
            val (start, end) = buffer.splitAt(left)
            left = 0
            val group = buffer ++ start
            buffer = null
            Iterable(group, end)
          }
        }
      })
  }

}

