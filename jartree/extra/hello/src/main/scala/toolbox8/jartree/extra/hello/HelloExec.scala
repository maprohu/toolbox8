package toolbox8.jartree.extra.hello

import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import toolbox8.jartree.extra.shared.ExecProtocol.Executable

/**
  * Created by maprohu on 02-11-2016.
  */
class HelloExec extends Executable[Any] with LazyLogging {
  logger.info("created")
  override def flow(ctx: Any): Flow[ByteString, ByteString, _] = {
    logger.info("creating flow")
    Flow
      .fromSinkAndSource(
        Sink.ignore,
        Source
          .single(
            ByteString(
              s"hello from: ${ctx}\n"
            )
          )
          .map({ o =>
            logger.info(s"sending: ${o}")
            o
          })
      )
  }
}
