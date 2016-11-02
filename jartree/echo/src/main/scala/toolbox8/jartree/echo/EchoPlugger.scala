package toolbox8.jartree.echo


import akka.stream.scaladsl.Flow
import akka.util.ByteString
import org.reactivestreams.Processor
import toolbox6.jartree.api.{JarPlugResponse, JarPlugger, PullParams}
import toolbox8.jartree.standaloneapi.{JarTreeStandaloneContext, PeerInfo, Service}
import monix.execution.Scheduler.Implicits.global
import monix.reactive.subjects.{PublishToOneSubject, Subject}
import toolbox6.jartree.util.JarTreeTools

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by pappmar on 17/10/2016.
  */
class EchoPlugger
  extends JarPlugger[Service, JarTreeStandaloneContext]
{
  override def pull(
    params: PullParams[Service, JarTreeStandaloneContext]
  ): Future[JarPlugResponse[Service]] = {
    import params._
    Future.successful(
      JarTreeTools.andThenResponse(
        new EchoService,
        () => previous.close()
      )
    )
  }
}

class EchoService(implicit
  executionContext: ExecutionContext
) extends Service {
  override def close(): Unit = ()
  override def apply(input: PeerInfo) = Future.successful(Flow[ByteString])

}
