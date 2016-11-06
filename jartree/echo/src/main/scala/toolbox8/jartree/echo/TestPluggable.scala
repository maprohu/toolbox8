package toolbox8.jartree.echo

import toolbox8.jartree.akka.PluggableServiceActor.{PlugContext, Pluggable, Plugged, VoidPlugged}

import scala.concurrent.Future

/**
  * Created by maprohu on 06-11-2016.
  */
class TestPluggable extends Pluggable {
  override def plug(context: PlugContext): Future[Plugged] = {
    println("csuf")
    Future.successful(
      VoidPlugged
    )
  }
}
