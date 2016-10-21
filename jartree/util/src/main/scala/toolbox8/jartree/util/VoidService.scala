package toolbox8.jartree.util

import monix.reactive.Observable
import org.reactivestreams.Processor
import toolbox6.javaapi.AsyncValue
import toolbox6.javaimpl.JavaImpl
import toolbox8.jartree.standaloneapi.Message._
import toolbox8.jartree.standaloneapi.{JarTreeStandaloneContext, Message, PeerInfo, Service}
import monix.execution.Scheduler.Implicits.global
import monix.reactive.MulticastStrategy.Async
import toolbox6.jartree.api.{JarPlugResponse, JarPlugger}

/**
  * Created by pappmar on 17/10/2016.
  */

object VoidService extends VoidService
class VoidService extends Service {
//  override def update(param: Array[Header]): Unit = ()
  override def apply(input: PeerInfo): AsyncValue[Processor[Message, Message]] = {
    JavaImpl
      .asyncSuccess(
        JavaImpl
          .processor(
            monix.reactive.observers.Subscriber
              .toReactiveSubscriber(
                monix.reactive.observers.Subscriber.empty[Message]
              ),
            Observable
              .empty[Message]
              .toReactivePublisher
          )

      )
  }

  override def close(): Unit = ()

//  override def updateAsync(param: Array[Header]): AsyncValue[Unit] = JavaImpl.asyncSuccess()
}

class VoidServicePlugger
  extends JarPlugger[Service, JarTreeStandaloneContext] {
  override def pullAsync(previous: Service, context: JarTreeStandaloneContext): AsyncValue[JarPlugResponse[Service]] = {
    JavaImpl.asyncSuccess(
      new JarPlugResponse[Service] {
        override def instance(): Service = VoidService
        override def andThen(): Unit = previous.close()
      }
    )
  }
}