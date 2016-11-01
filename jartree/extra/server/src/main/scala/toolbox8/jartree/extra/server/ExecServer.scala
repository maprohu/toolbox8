package toolbox8.jartree.extra.server

import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString
import toolbox6.jartree.api.{ClassRequest, InstanceResolver}
import toolbox8.akka.statemachine.AkkaStreamCoding
import toolbox8.jartree.extra.shared.ExecProtocol.Executable

/**
  * Created by maprohu on 01-11-2016.
  */
object ExecServer {

  def flow(
    instanceResolver: InstanceResolver
  ) = {
    Flow[ByteString]
      .prefixAndTail(1)
      .flatMapConcat({
        case (heads, tail) =>
          val head = heads.head
          import toolbox6.pickling.PicklingTools._
          Source
            .fromFuture(
              instanceResolver
                .resolveAsync(
                  Unpickle[ClassRequest[Executable]]
                    .fromBytes(head.asByteBuffer)
                )
            )
            .flatMapConcat({ e =>
              tail
                .via(e.flow)
            })
      })
  }

}
