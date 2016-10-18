package toolbox8.akka.statemachine

import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import monix.eval.{Callback, Task}
import monix.execution.{CancelableFuture, Scheduler}
import monix.reactive.observables.ConnectableObservable
import monix.reactive.observers.{BufferedSubscriber, CacheUntilConnectSubscriber, ConnectableSubscriber, Subscriber}
import monix.reactive.subjects.PublishToOneSubject
import monix.reactive.{Consumer, Observable, Observer, OverflowStrategy}
import toolbox6.logging.LogTools
import toolbox6.statemachine.{State, StateAsync}

import scala.concurrent.{ExecutionContext, Future, Promise}

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

  val End = StateAsync.end[ByteString, ByteString]()

}

object ByteStrings {
  def mapPrefix[T](bs: ByteString)(n: Int)(fn: ByteString => T) : (T, ByteString) = {
    (fn(bs), bs.drop(n))
  }

  def getByteHeader(bs: ByteString) : (Byte, ByteString) =
    mapPrefix(bs)(1)({ h =>
      h(0)
    })

  implicit class ByteStringOps(bs: ByteString) {
    def mapPrefix[T](n: Int)(fn: ByteString => T) : (T, ByteString) = {
      (fn(bs), bs.drop(n))
    }
    def getByteHeader : (Byte, ByteString) =
      mapPrefix(1)({ h =>
        h(0)
      })
    def getIntHeader : (Int, ByteString) =
      mapPrefix(4)({ h =>
        (h(0) << 24) |
          ((h(1) & 0xff) << 16) |
          ((h(2) & 0xff) << 8) |
          (h(3) & 0xff)
      })
  }

  implicit class ObsOps[T](o: Observable[T]) {
    def consume[S](
      c: Consumer[T, S]
    )(implicit
      scheduler: Scheduler
    ) : Task[S] = {
      val p = Promise[S]()
      val (s, cc) =
        c.createSubscriber(
          Callback.fromPromise(p),
          scheduler
        )

      o
        .subscribe(
          s
        )

      Task.create[S]({ (s, cb) =>
        p
          .future
          .onComplete(cb)

        cc
      })
    }
  }

}

object DeepStream extends LazyLogging with LogTools {

  val MaxChunkSize = 1024 * 16
  val NonLast : Byte = 0
  val Last : Byte = 1
  val Error : Byte = 2

//  case class State(
//    observer: Observer[ByteString],
//    next: Future[FlatState]
//  ) {
//    lazy val empty = FlatState(
//      this,
//      Observable.empty
//    )
//  }
//
//  case class FlatState(
//    state: State,
//    out: Observable[ByteString] = Observable.empty
//  )

//  def stateMachine(
//    init: FlatState
//  )(implicit
//    executionContext: ExecutionContext
//  ) : Observable[ByteString] => Observable[ByteString] = { o =>
//    import ByteStrings._
//
//    Observable
//      .concat(
//        init.out,
//        o
//          .onErrorHandle({ ex =>
//            logger.error(ex.getMessage, ex)
//            ByteString(Error)
//          })
//          .flatScan(
//            init.state.empty
//          )({ (fstate, elem) =>
//            val (head, data) = elem.getByteHeader
//
//            val obs =
//              fstate
//                .state
//                .observer
//
//            def send =
//              obs
//                .onNext(
//                  data
//                )
//
//            head match {
//              case Last =>
//                Observable
//                  .fromFuture(
//                    send
//                      .flatMap({ _ =>
//                        obs.onComplete()
//                        fstate.state.next
//                      })
//                      .map({
//                        case (out, newState) =>
//                          FlatState(
//                            out = out,
//                            state = newState
//                          )
//                      })
//
//                  )
//              case NonLast =>
//                Observable
//                  .fromFuture(
//                    send
//                      .map({ _ =>
//                        fstate.state.empty
//                      })
//                  )
//              case Error =>
//                val ex = new RuntimeException("upstream error")
//                obs.onError(ex)
//                throw ex
//              case _ => ???
//
//            }
//
//          })
//          .flatMap({ o =>
//            o
//              .out
//              .transform(chunks)
//          })
//      )
//  }

  val chunks : Observable[ByteString] => Observable[ByteString] = { o =>
    Observable
      .concat(
        o
          .map(bs => ByteString(NonLast) ++ bs),
        Observable(
          ByteString(Last)
        )
      )
  }

//  def strict(
//    next: ByteString => Future[FlatState]
//  ) : Future[State] = {
//    val subject = PublishToOneSubject[ByteString]()
//
//
//    val connectable =
//      ConnectableObservable(subject)
//
//    val s = State(
//      subject,
//      subject
//        .foldLeftL(ByteString.empty)(_ ++ _)
//        .runAsync
//        .flatMap(next)
//    )
//
//    subject
//      .subscription
//      .map(_ => s)
//  }

  def encode(bs: ByteString) : Observable[ByteString] = {
    Observable.fromIterator {
      val ci = bs
        .grouped(MaxChunkSize)

      new Iterator[ByteString] {
        override def hasNext: Boolean = ci.hasNext
        override def next(): ByteString = {
          val c = ci.next()
          if (ci.hasNext) {
            ByteString(NonLast)
          } else {
            ByteString(Last)
          } ++ c
        }
      }
    }
  }

  def encode(bso: Observable[ByteString]) : Observable[ByteString] = {
    bso
      .flatMap({ bs =>
        Observable
          .fromIterator(bs.grouped(MaxChunkSize))
          .map(c => ByteString(NonLast) ++ c)
      }) ++ Observable(ByteString(Last))
  }


  case class FState(
    observer: Observer[ByteString],
    out: Option[Observable[ByteString]] = None
  ) {
    lazy val empty = out.map(_ => copy(out = None)).getOrElse(this)
  }

  def newFState(implicit
    scheduler: Scheduler
  ) = {
    val subject = PublishToOneSubject[ByteString]()
    val subscriber = Subscriber(subject, scheduler)
    val buffered = CacheUntilConnectSubscriber(subscriber)
    subject
      .subscription
      .foreach({ _ =>
        buffered.connect()
      })

    FState(
      buffered,
      Some(subject)
    )
  }

  def decoder(implicit
    scheduler: Scheduler
  ) : Observable[ByteString] => Observable[Observable[ByteString]] = { o =>

    val start = newFState

    import ByteStrings._

    Observable
      .concat(
        Observable(start),
        o
          .onErrorHandle({ ex =>
            logger.error(ex.getMessage, ex)
            ByteString(Error)
          })
          .flatScan(
            start.empty
          )({ (fstate, elem) =>
            val (head, data) = getByteHeader(elem)

            val obs =
              fstate
                .observer

            def send = {
              obs
                .onNext(
                  data
                )
            }

            head match {
              case Last =>
                Observable
                  .fromFuture(
                    send
                      .map({ _ =>
                        obs.onComplete()
                        newFState
                      })
                  )
              case NonLast =>
                Observable
                  .fromFuture(
                    send
                      .map({ _ =>
                        fstate.empty
                      })
                  )
              case Error =>
                val ex = new RuntimeException("upstream error")
                obs.onError(ex)
                throw ex
              case _ => ???

            }

          })
      )
      .asyncBoundary(OverflowStrategy.BackPressure(2))
      .flatMap(o => Observable.fromIterable(o.out.toIterable))
      .asyncBoundary(OverflowStrategy.BackPressure(2))

  }

  val concat : Consumer[ByteString, ByteString] = Consumer.foldLeft(ByteString.empty)(_ ++ _)


  val strict : Observable[Observable[ByteString]] => Observable[ByteString] = { o =>
    o
      .flatMap({ bso =>
        Observable
          .fromTask(
            bso.runWith(concat)
          )
      })
  }

  case class State(
    task: Observable[ByteString]

  )

//  def stateMachine(
//    state: Init
//
//  ) : Observable[Observable[ByteString]] => Observable[ByteString] = { o =>
//
//  }

}