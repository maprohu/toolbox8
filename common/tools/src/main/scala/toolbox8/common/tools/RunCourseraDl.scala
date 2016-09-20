package toolbox8.common.tools

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpHeader, HttpMethods, HttpRequest}
import akka.stream.ActorMaterializer

import scala.util.Random

/**
  * Created by martonpapp on 20/09/16.
  */
object RunCourseraDl {

  def main(args: Array[String]): Unit = {

    implicit val actorSystem = ActorSystem()
    implicit val materializer = ActorMaterializer()
    import actorSystem.dispatcher

    for {
      res <- Http().singleRequest(
        HttpRequest(
          method = HttpMethods.POST,
          uri = "https://www.coursera.org/api/login/v3"
        )
      )

    } {
      println(res)
    }

  }

  val letters = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')

  def randomString(length: Int) : String = {
    Iterator
      .continually(letters(Random.nextInt(letters.size)))
      .take(length)
      .mkString
  }

  def csrf : Seq[HttpHeader] = {
    ???

  }

}
