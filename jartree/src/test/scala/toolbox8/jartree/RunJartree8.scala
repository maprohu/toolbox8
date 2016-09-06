package toolbox8.jartree

import java.io.File

import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.{ActorMaterializer, Attributes}
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import jartree.impl.{JarCache, JarResolver, JarTree}
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

    val jarTree = JarTree(
      RunJartree8.getClass.getClassLoader,
      JarResolver(
        JarCache(
          new File("../toolbox8/target/jartree8/cache")
        )
      )
    )


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
    val server : Flow[ByteString, ByteString, NotUsed] = JarTree8.server(jarTree)


    client
      .log("client2server").withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
      .join(server)
      .run()

  }



}
