package toolbox8.tomcat.testing

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{BinaryMessage, WebSocketRequest}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.ByteString

/**
  * Created by pappmar on 13/03/2017.
  */
object RunJarServletClient {

  def main(args: Array[String]): Unit = {

    implicit val actorSystem = ActorSystem()
    implicit val materializer = ActorMaterializer()
    import actorSystem.dispatcher

    val flow =
      Flow.fromSinkAndSource(
        Sink.ignore,
        Source.single(
          BinaryMessage(ByteString())
        )
      )

    val (response, _) =
      Http()
        .singleWebSocketRequest(
          WebSocketRequest("ws://localhost:8080/private/ws"),
          flow
        )

    response.onComplete(println)


  }

}
