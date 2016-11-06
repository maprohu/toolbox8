package toolbox8.jartree.echo

import com.typesafe.scalalogging.LazyLogging
import toolbox8.jartree.akka.PluggableServiceActor.{PlugContext, Pluggable, Plugged, VoidPlugged}

import scala.concurrent.Future

/**
  * Created by maprohu on 06-11-2016.
  */
class TestPluggable extends Pluggable with LazyLogging {
  override def plug(context: PlugContext): Future[Plugged] = {
    logger.info(s"${toString} plugging")
    Future.successful(
      VoidPlugged
    )
  }
}
