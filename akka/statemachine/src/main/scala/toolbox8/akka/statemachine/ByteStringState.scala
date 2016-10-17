package toolbox8.akka.statemachine

import akka.util.ByteString
import monix.eval.Task
import monix.reactive.Observable
import toolbox6.statemachine.{State, StateAsync}

/**
  * Created by pappmar on 17/10/2016.
  */
object ByteStringState {
  type BSState = State[ByteString, ByteString]
  type BSAState = StateAsync[ByteString, ByteString]

  def state(
    out: Observable[ByteString] = Observable.empty,
    fn: ByteString => BSState
  ) : BSState = State(out, fn)

  def stateAsync(
    out: Observable[ByteString] = Observable.empty,
    fn: ByteString => Task[BSAState]
  ) : BSAState = StateAsync(out, fn)

  def takeAsync[S](
    n: Long,
    andThen: BSAState,
    chunk: (BSAState, ByteString, Long) => Task[BSAState]
  ) : BSAState = {
    require(n > 0)

    def inner(
      n: Long,
      andThen: BSAState
    ) : BSAState = {
      stateAsync(
        fn = { bs =>
          if (bs.size < n) {
            val remaining = n - bs.size
            chunk(
              andThen,
              bs,
              remaining
            )
            .map({ s =>
              inner(
                remaining,
                s
              )
            })
          } else if (bs.size == n) {
            chunk(
              andThen,
              bs,
              0
            )
          } else {
            val (last, next) = bs.splitAt(n.toInt)
            chunk(
              andThen,
              last,
              0
            ).flatMap({ s =>
              s
                .fn(next)
                .map({ s2 =>
                  stateAsync(
                    out = Observable.concat(
                      s.out,
                      s2.out
                    ),
                    fn = s2.fn
                  )
                })
            })
          }
        }
      )
    }

    inner(n, andThen)
  }

}
