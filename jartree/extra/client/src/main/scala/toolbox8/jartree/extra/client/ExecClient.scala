package toolbox8.jartree.extra.client

import akka.event.Logging
import akka.stream.Attributes
import akka.stream.scaladsl.{Flow, Keep, Source}
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import toolbox6.jartree.api.ClassRequest
import toolbox8.jartree.extra.shared.ExecProtocol.Executable

import scala.concurrent.Promise

/**
  * Created by maprohu on 01-11-2016.
  */
object ExecClient extends LazyLogging {

  def source[Ctx](
    classRequest: ClassRequest[Executable[Ctx]]
  ) : Source[ByteString, Promise[Option[Unit]]] = {
    import toolbox6.pickling.PicklingTools._
    Source
      .maybe[Unit]
      .map({ _ =>
        logger.info("sending exec request")
        ByteString(
          Pickle[ClassRequest[Executable[Ctx]]](classRequest)
            .toByteBuffer
        )
      })
//      .log("exec-source").withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
  }

  def flow[Ctx](
    classRequest: ClassRequest[Executable[Ctx]],
    data: Flow[ByteString, ByteString, _]
  ) : Flow[ByteString, ByteString, Promise[Option[Unit]]] = {
    data
      .viaMat(
        Flow[ByteString]
          .prependMat(
            source(classRequest)
          )(Keep.right)
      )(Keep.right)

  }


}
