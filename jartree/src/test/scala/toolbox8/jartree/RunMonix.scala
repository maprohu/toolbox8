package toolbox8.jartree

import java.nio.ByteOrder

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.{ActorMaterializer, Attributes}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.ByteString
import monix.execution.Cancelable
import monix.execution.cancelables.BooleanCancelable
import monix.reactive.{Observable, Observer, Pipe}
import toolbox8.akka.stream.Streams
import monix.execution.Scheduler.Implicits.global
import monix.reactive.observers.Subscriber

/**
  * Created by pappmar on 07/09/2016.
  */
object RunMonix {

  def main(args: Array[String]): Unit = {

    implicit val actorSystem = ActorSystem()
    implicit val actorMaterializer = ActorMaterializer()
    implicit val byteOrder = ByteOrder.BIG_ENDIAN


    val source =
      Source
        .fromIterator(() => Iterator.from(0))
        .map({ n =>
          ByteString
            .newBuilder
            .putInt(n)
            .result()
        })

//    source
//      .runForeach(println)

//    val publisher =
//      source
//        .runWith(
//          Sink.asPublisher(false)
//        )
//
//    Observable
//      .fromReactivePublisher(publisher)
//      .map({ bs =>
//        Streams.toInt(bs.toArray)
//      })
//      .foreach(println)

    val processor =
      Flow
        .fromSinkAndSource(
          Sink.foreach[ByteString](println),
//          Flow[ByteString]
//            .log("sink").withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
//            .to(Sink.ignore),
          source
        )
        .toProcessor
        .run()

    val observable =
      Observable
        .fromReactivePublisher(processor)
        .multicast(Pipe.publish[ByteString])

    observable.connect()

    observable
      .subscribe(
        Observer.dump("O")
      )

    val subscriber =
      Subscriber
        .fromReactiveSubscriber(processor, Cancelable.empty)

    observable
      .subscribe(subscriber)


  }

}
