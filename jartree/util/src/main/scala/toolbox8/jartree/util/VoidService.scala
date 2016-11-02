package toolbox8.jartree.util

import akka.stream.scaladsl.Flow
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
    Future.successful(Flow[ByteString])
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