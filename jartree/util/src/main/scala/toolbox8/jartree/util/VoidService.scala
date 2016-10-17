package toolbox8.jartree.util

import monix.reactive.Observable
import org.reactivestreams.Processor
import toolbox6.javaapi.AsyncValue
import toolbox6.javaimpl.JavaImpl
import toolbox8.jartree.standaloneapi.Message._
import toolbox8.jartree.standaloneapi.{Message, PeerInfo, Service}
import monix.execution.Scheduler.Implicits.global

/**
  * Created by pappmar on 17/10/2016.
  */

object VoidService extends VoidService
class VoidService extends Service {
  override def update(param: Array[Header]): Unit = ()
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
}
