package toolbox8.akka.statemachine

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.{ActorMaterializer, Attributes}
import akka.stream.Attributes.LogLevels
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.ByteString

import scala.util.Random

/**
  * Created by pappmar on 18/10/2016.
  */
object RunAkkaStreamCoding {

  def main(args: Array[String]): Unit = {
    import toolbox8.akka.statemachine.AkkaStreamCoding.Terminal
    implicit val actorSystem = ActorSystem()
    implicit val materializer = ActorMaterializer()


//    val data = RunStreamCoding.generate(10, () => Random.nextInt(100) + 100)
    val data = RunStreamCoding.generate(2, () => 100000)

    Source(data)
      .log("in").withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
      .map(bs => ByteString(0.toByte) ++ bs)
      .via(
        AkkaStreamCoding.Multiplex.flow(
          Flow[ByteString]
            .map(bs => Source.single(ByteString(0.toByte) ++ bs))
            .via(Terminal.encoder)
            .via(Terminal.decoder)
            .via(Terminal.concat)
        )
      )
      .log("out").withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
      .to(Sink.ignore)
      .run()

    Flow[ByteString]
      .prefixAndTail(0)
  }

}
