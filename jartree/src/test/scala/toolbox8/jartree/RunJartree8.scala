package toolbox8.jartree

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import jartree.util.{CaseClassLoaderKey, HashJarKeyImpl, RunRequestImpl}

import scala.collection.immutable._

/**
  * Created by pappmar on 06/09/2016.
  */
object RunJartree8 {

  def main(args: Array[String]): Unit = {
    implicit val actorSystem = ActorSystem()
    implicit val materializer = ActorMaterializer()
    import actorSystem.dispatcher

    val client : Flow[ByteString, ByteString, NotUsed] = JarTree8.client(
      RunRequestWithAttachments(
        RunRequestImpl(
          CaseClassLoaderKey(
            HashJarKeyImpl(
              Seq()
            ),
            Seq()
          ),
          "boo"
        ),
        Seq()
      )
    )
    val server : Flow[ByteString, ByteString, NotUsed] = JarTree8.server


    client
      .join(server)
      .run()

  }

}
