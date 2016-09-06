package toolbox8.jartree

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import akka.util.ByteString

/**
  * Created by pappmar on 06/09/2016.
  */
object RunJartree8 {

  def main(args: Array[String]): Unit = {

    val client : Flow[ByteString, ByteString, NotUsed] = ???
    val server : Flow[ByteString, ByteString, NotUsed] = ???

    implicit val actorSystem = ActorSystem()
    implicit val materializer = ActorMaterializer()

    client
      .join(server)
      .run()

  }

}
