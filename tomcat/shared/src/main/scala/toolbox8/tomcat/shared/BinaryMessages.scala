package toolbox8.tomcat.shared

import akka.http.scaladsl.model.ws.BinaryMessage
import akka.stream.Materializer
import akka.stream.scaladsl.Flow

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by pappmar on 14/03/2017.
  */
object BinaryMessages {

  def toStrict(m: BinaryMessage)(implicit
    materializer: Materializer
  ) : Future[BinaryMessage.Strict] = {
    import materializer.executionContext
    m match {
      case s : BinaryMessage.Streamed =>
        s
          .dataStream
          .runReduce(_ ++ _)
          .map(BinaryMessage.apply)

      case s : BinaryMessage.Strict =>
        Future.successful(s)
    }
  }


  def toStrictFlow(implicit
    materializer: Materializer
  ) = {
    Flow[BinaryMessage]
      .mapAsync(1)({ m =>
        toStrict(m)
      })
  }
}
