package toolbox8.jartree.testing

import toolbox8.jartree.requestapi.RequestMarker
import toolbox8.jartree.streamapp.{PlugParams, Plugged, Root}

/**
  * Created by maprohu on 21-11-2016.
  */
class TestingRoot extends Root {

  override def plug(params: PlugParams): Plugged = {
    println(s"plug: ${params}")

    new Plugged {
      override def preUnplug: Any = {
        println("preUnplug")
      }

      override def postUnplug: Unit = {
        println("postUnplug")
      }

      override def marked[In, Out](marker: RequestMarker[In, Out], in: In): Out = ???
    }
  }

}
