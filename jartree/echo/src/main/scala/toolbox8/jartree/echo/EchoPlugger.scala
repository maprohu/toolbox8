package toolbox8.jartree.echo


import org.reactivestreams.Processor
import toolbox6.jartree.api.{JarPlugResponse, JarPlugger}
import toolbox6.jartree.util.JarTreeTools
import toolbox6.javaapi.AsyncValue
import toolbox8.jartree.standaloneapi.{JarTreeStandaloneContext, Message, PeerInfo, Service}
import monix.execution.Scheduler.Implicits.global
import monix.reactive.subjects.{PublishToOneSubject, Subject}
import toolbox6.javaimpl.JavaImpl

/**
  * Created by pappmar on 17/10/2016.
  */
class EchoPlugger
  extends JarPlugger[Service, JarTreeStandaloneContext]
{
  override def pull(previous: Service, param: Array[Byte], context: JarTreeStandaloneContext): JarPlugResponse[Service] = {
    JarTreeTools.closableResponse(new EchoService, previous)
  }
}

class EchoService extends Service {
  override def close(): Unit = ()
  override def update(param: Array[Byte]): Unit = ()
  override def apply(input: PeerInfo): AsyncValue[Processor[Message, Message]] = {
    JavaImpl
      .asyncSuccess(
        PublishToOneSubject[Message]()
          .toReactivePublisher
      )
  }
}
