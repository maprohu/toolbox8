package toolbox8.akka.statemachine

import akka.util.ByteString
import monix.execution.atomic.Atomic
import org.reactivestreams.{Processor, Publisher, Subscriber, Subscription}

import scala.collection.mutable
import scala.concurrent.Future

///**
//  * Created by pappmar on 18/10/2016.
//  */
//object LowLevelStateMachine extends LowLevelReactiveTools{
//
//
//  /**
//    * (framing)
//    * multiplexing
//    * term-coding
//    * state-machine
//    *
//    *
//    */
//  def connect(
//    init: State,
//    peer: Processor[ByteString, ByteString]
//  ) = {
//    peer.subscribe(
//      subscriber(
//
//      )
//    )
//
//  }
//
//  case class State(
//    out: Publisher[ByteString],
//    transition: Publisher[ByteString] => Future[State]
//  )
//
//}
//
//trait LowLevelReactiveTools {
//
//  def checkNotNull(ref: Any) = {
//    if (ref == null) throw new NullPointerException
//  }
//
//  def subscriber[T](
//    doOnSubscribe: Subscription => Unit,
//    doOnError: Throwable => Unit,
//    doOnComplete: () => Unit,
//    doOnNext: T => Unit
//  ) = new Subscriber[T] {
//    override def onError(t: Throwable): Unit = {
//      checkNotNull(t)
//      doOnError(t)
//    }
//
//    val subscription = Atomic(null:Subscription)
//    override def onSubscribe(s: Subscription): Unit = {
//      checkNotNull(s)
//      subscription.transform({ current =>
//        if (current == null) {
//          doOnSubscribe(s)
//          s
//        } else {
//          s.cancel()
//          current
//        }
//      })
//    }
//
//    override def onComplete(): Unit = {
//      doOnComplete()
//    }
//
//    override def onNext(t: T): Unit = {
//      checkNotNull(t)
//      doOnNext(t)
//    }
//
//  }
//}
//

