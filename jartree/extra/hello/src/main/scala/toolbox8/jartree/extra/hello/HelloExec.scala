package toolbox8.jartree.extra.hello

import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.ByteString
import toolbox8.jartree.extra.shared.ExecProtocol.Executable

/**
  * Created by maprohu on 02-11-2016.
  */
class HelloExec extends Executable[Any] {
  override def flow(ctx: Any): Flow[ByteString, ByteString, _] = {
    Flow
      .fromSinkAndSource(
        Sink.ignore,
        Source
          .single(
            ByteString(
              s"hello from: ${ctx}\n"
            )
          )
      )
  }
}
