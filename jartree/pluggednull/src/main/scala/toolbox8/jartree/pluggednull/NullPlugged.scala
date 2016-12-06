package toolbox8.jartree.pluggednull

import toolbox8.jartree.requestapi.RequestMarker
import toolbox8.jartree.streamapp.{PlugParams, Plugged, Root}

/**
  * Created by pappmar on 06/12/2016.
  */
class NullPlugged extends Plugged {

  override def marked[In, Out](marker: RequestMarker[In, Out], in: In): Out = ???
  override def stop(): Unit = ()

}

class NullPluggedRoot extends Root {
  override def plug(params: PlugParams): Plugged = {
    new NullPlugged
  }
}
