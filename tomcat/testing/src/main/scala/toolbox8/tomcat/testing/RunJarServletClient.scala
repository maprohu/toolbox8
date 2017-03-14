package toolbox8.tomcat.testing

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, WebSocketRequest}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.ByteString
import toolbox8.tomcat.shared.{BinaryMessages, PicklingClient, VerifyJars}

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
        Flow[Message]
          .map({
            case b : BinaryMessage => b
            case _ => ???
          })
          .via(BinaryMessages.toStrictFlow)
          .map({ m =>
            PicklingClient.unpickle(m.data.asByteBuffer)
          })
          .to(
            Sink.foreach(println)
          ),

        Source
          .single(
            VerifyJars(
              Seq()
            )
          )
          .concat(Source.maybe)
          .map({ m =>
            BinaryMessage(
              ByteString(
                PicklingClient.pickle(m)
              )
            )
          })
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
