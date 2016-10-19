package toolbox8.jartree.echo


import org.reactivestreams.Processor
import toolbox6.jartree.api.{JarPlugResponse, JarPlugger}
import toolbox6.jartree.util.{Closable, JarTreeTools}
import toolbox6.javaapi.AsyncValue
import toolbox8.jartree.standaloneapi.{JarTreeStandaloneContext, Message, PeerInfo, Service}
import monix.execution.Scheduler.Implicits.global
import monix.reactive.subjects.{PublishToOneSubject, Subject}
import toolbox6.javaimpl.JavaImpl

import scala.concurrent.ExecutionContext

/**
  * Created by pappmar on 17/10/2016.
  */
class EchoPlugger
  extends JarPlugger[Service, JarTreeStandaloneContext]
{
//  override def pull(previous: Service, param: Array[Byte], context: JarTreeStandaloneContext): JarPlugResponse[Service] = {
//    JarTreeTools.closableResponse(new EchoService, previous)
//  }
  override def pullAsync(previous: Service, param: Array[Byte], context: JarTreeStandaloneContext): AsyncValue[JarPlugResponse[Service]] = {
    JavaImpl.asyncSuccess(
      JarTreeTools.andThenResponse(
        new EchoService,
        () => previous.close()
      )
    )
  }
}

class EchoService(implicit
  executionContext: ExecutionContext
) extends Service with Closable {
  override def close(): Unit = ()
//  override def update(param: Array[Byte]): Unit = ()
  override def apply(input: PeerInfo): AsyncValue[Processor[Message, Message]] = {
    JavaImpl
      .asyncSuccess(
        PublishToOneSubject[Message]()
          .toReactivePublisher
      )
  }

  override def updateAsync(param: Array[Byte]): AsyncValue[Unit] = JavaImpl.asyncSuccess()
}
