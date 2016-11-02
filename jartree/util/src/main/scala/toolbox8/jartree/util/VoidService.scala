package toolbox8.jartree.util

import akka.event.Logging
import akka.stream.Attributes
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.ByteString
import toolbox6.jartree.api.{JarPlugResponse, JarPlugger, PullParams}
import toolbox8.jartree.standaloneapi.{JarTreeStandaloneContext, PeerInfo, Service}

import scala.concurrent.Future

/**
  * Created by pappmar on 17/10/2016.
  */

object VoidService extends VoidService
class VoidService extends Service {
//  override def update(param: Array[Header]): Unit = ()
  override def apply(input: PeerInfo) = {
    Future.successful(
      Flow
        .fromSinkAndSource(
          Flow[ByteString]
            .log("void-service")
            .withAttributes(Attributes.logLevels(onElement = Logging.WarningLevel))
            .to(Sink.ignore),
          Source.maybe
        )
    )
  }

  override def close(): Unit = ()

//  override def updateAsync(param: Array[Header]): AsyncValue[Unit] = JavaImpl.asyncSuccess()
}

class VoidServicePlugger
  extends JarPlugger[Service, JarTreeStandaloneContext] {
  override def pull(
    params: PullParams[Service, JarTreeStandaloneContext]
  ): Future[JarPlugResponse[Service]] = {
    import params._
    Future.successful(
      JarPlugResponse[Service](
        VoidService,
        () => previous.close()
      )
    )
  }
}