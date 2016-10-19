package toolbox8.jartree.standalone

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.file.Path
import java.util

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Materializer, Supervision}
import akka.stream.scaladsl.{FileIO, Flow, Keep, Sink, Source, Tcp}
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import monix.execution.{Cancelable, Scheduler}
import monix.reactive.Observable
import monix.reactive.observers.Subscriber
import toolbox6.jartree.impl.{JarCache, JarTree, JarTreeBootstrap}
import toolbox8.jartree.protocol.JarTreeStandaloneProtocol
import toolbox8.jartree.standaloneapi.{JarTreeStandaloneContext, Message, PeerInfo, Service}
import org.reactivestreams.Processor
import toolbox6.jartree.api.{ClassRequest, JarPlugger}
import toolbox6.jartree.impl.JarTreeBootstrap.Config
import toolbox6.jartree.util.{CaseJarKey, ScalaInstanceResolver}
import toolbox6.jartree.wiring.{PlugRequestImpl, SimpleJarSocket}
import toolbox6.javaapi.{AsyncCallback, AsyncValue}
import toolbox6.javaimpl.JavaImpl
import toolbox6.statemachine.State
import toolbox8.akka.statemachine.AkkaStreamCoding
import toolbox8.akka.statemachine.AkkaStreamCoding.{Data, StateMachine}
import toolbox8.akka.statemachine.AkkaStreamCoding.StateMachine.{StateOut, Transition}
import toolbox8.akka.stream.AkkaStreamTools
import toolbox8.jartree.protocol.JarTreeStandaloneProtocol.Management
import toolbox8.jartree.protocol.JarTreeStandaloneProtocol.Management.{Done, Plug, VerifyRequest, VerifyResponse}
import toolbox8.jartree.standaloneapi.Message.Header
import toolbox8.jartree.util.VoidService

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.collection.immutable._

/**
  * Created by martonpapp on 15/10/16.
  */

object JarTreeStandalone extends LazyLogging {

  trait CTX extends JarTreeStandaloneContext with ScalaInstanceResolver

  def run(
    name: String,
    port: Int,
    version: Int = -1,
    embeddedJars: Seq[(CaseJarKey, () => InputStream)],
    initialStartup: PlugRequestImpl[Service, CTX]
  )(implicit
    scheduler: Scheduler
  ) = {
    val rt = JarTreeBootstrap
      .init[Service, CTX](
      Config[Service, CTX](
        jarTree => new CTX {
          override def resolve[T](request: ClassRequest[T]): Future[T] = jarTree.resolve(request)
          override implicit def executionContext: ExecutionContext = scheduler
        }: CTX,
        voidProcessor = VoidService,
        name = name,
        dataPath = s"/opt/${name}/data",
        version = version,
        embeddedJars = embeddedJars,
        initialStartup = initialStartup,
        runtimeVersion = JarTreeStandalone.getClass.getPackage.getImplementationVersion,
        closer = _.close()
      )
    )

    val cmps = AkkaStreamTools.bootstrap()
    import cmps._

    Tcp()
      .bind(
        "0.0.0.0",
        port
      )
      .mapAsync(1)({ incoming =>
        logger.info("incoming: {}", incoming.remoteAddress)
        JavaImpl
          .unwrapFunction(
            rt
              .processorSocket
              .get()
          )(
            new PeerInfo {
              override def address() = incoming.remoteAddress
            }
          )
          .map({ p =>
            (incoming.flow, p)
          })
      })
      .toMat(
        Sink.foreach({
          case (peerFlow, dataProc) =>
            val management = createManagement(
              rt.jarTree,
              rt.processorSocket
            )
            val data : Flow[ByteString, ByteString, Any] = Flow[ByteString]

            peerFlow
              .join(AkkaStreamCoding.framing.reversed)
              .join(
                AkkaStreamCoding
                  .Multiplex
                  .flow(
                    management,
                    data
                  )
              )
              .run()
        })
      )(Keep.left)
      .run()
      .onComplete({ res =>
        logger.info(s"binding complete: ${res}")
      })
  }

  import AkkaStreamCoding.StateMachine.State
  import boopickle.Default._
  import AkkaStreamCoding.Implicits._

  def createManagement(
    jarTree: JarTree,
    socket: SimpleJarSocket[Service, CTX]
  )(implicit
    materializer: Materializer
  ) : Flow[ByteString, ByteString, Any] = {
    AkkaStreamCoding
      .Terminal
      .bidi
      .join(
        AkkaStreamCoding
          .StateMachine
          .flow(
            start(
              jarTree,
              socket
            )
          )
      )

  }

  def start(
    jarTree: JarTree,
    socket: SimpleJarSocket[Service, CTX]
  )(implicit
    materializer: Materializer
  ) : State = {
    import materializer.executionContext
    State(
      next = { data =>
        AkkaStreamCoding
           .unpickle[VerifyRequest](data)
            .map({ vq =>
              logger.info(s"verifying jars: ${vq.ids.mkString(", ")}")
              val (ids, idxs) = vq
                .ids
                .zipWithIndex
                .filter({
                  case (id, idx) if !jarTree.cache.contains(id) => true
                  case _ => false
                })
                .unzip

              logger.info(s"missing jars: ${ids.mkString(", ")}")

              State(
                out = Source.single(
                  AkkaStreamCoding.pickle(
                    VerifyResponse(
                      missing = idxs
                    )
                  )
                ),
                next = verifyRespone(
                  ids,
                  jarTree,
                  socket
                )
              )
            })
      }
    )
  }

  def verifyRespone(
    ids: Seq[String],
    jarTree: JarTree,
    socket: SimpleJarSocket[Service, CTX]
  )(implicit
    materializer: Materializer
  ) : Transition = {
    import materializer.executionContext

    AkkaStreamCoding
      .StateMachine
      .sequenceIn2(
        steps =
          ids
            .map({ id =>
              { data:Data =>
                logger.info(s"receiving: ${id}")
                val putOpt = jarTree.cache.putAsync(id)

                putOpt
                  .map({
                    case (target, promise) =>
                      data
                        .runWith(
                          FileIO
                            .toPath(target.toPath)
                        )
                        .map({ result =>
                          logger.info(s"received: ${id} - ${result}")
                          promise.success()
                          ()
                        })
                  })
                  .getOrElse({
                    logger.info(s"already cached, ignoring: ${id}")
                    data
                      .runWith(Sink.ignore)
                      .map({ _ =>
                        logger.info(s"ignored: ${id}")
                        ()
                      })
                  })
              }
            }),
        andThen = {
          waitPlug(
            jarTree,
            socket
          )
        }
      )


  }

  def waitPlug(
    jarTree: JarTree,
    socket: SimpleJarSocket[Service, CTX]
  )(implicit
    materializer: Materializer
  ) : Transition = { data =>
    import materializer.executionContext

    for {
      plug <-
        AkkaStreamCoding
          .unpickle[Plug](data)
      _ = logger.info(s"plugging: ${plug}")
      inst <- {
        jarTree.resolve(
          plug.classRequest
        )
      }
      _ = logger.info(s"plugger resolved: ${inst}")
      _ <- {
        socket.plug(
          PlugRequestImpl(
            plug.classRequest,
            plug.param
          )
        )
      }
    } yield {
      logger.info(s"plugged: ${plug}")
      State(
        out = {
          Source.single(
            AkkaStreamCoding.pickle(Done)
          )
        },
        next = _ => {
          Future.successful(StateMachine.End)
        }
      )
    }

  }


//        Sink.foreach({
//          case (peerFlow, dataProc) =>
//            val management = createManagement(
//              rt.jarTree.cache
//            )
//
//
//            val data = Multiplex.Layer(
//              headerCount = 0,
//              flow = { o =>
//                val out =
//                  Observable
//                    .fromReactivePublisher(
//                      dataProc
//                    )
//                    .map({ m =>
//                      Multiplex.Message(
//                        header = m.header(),
//                        data =
//                          m
//                            .data()
//                            .foldLeft(
//                              ByteString.empty
//                            )({ (bs, bb) =>
//                              bs ++ ByteString.fromByteBuffer(bb)
//                            })
//                      )
//                    })
//
//                o
//                  .map({ m =>
//                    new Message {
//                      override def data(): util.Enumeration[ByteBuffer] = {
//                        m
//                          .data
//                          .asByteBuffers
//                          .iterator
//                      }
//                      override def header(): Header = m.header.toByte
//                    }
//                  })
//                  .subscribe(
//                    Subscriber
//                      .fromReactiveSubscriber(
//                        dataProc,
//                        Cancelable.empty
//                      )
//                  )
//
//                out
//              }
//            )
//
//
//            val (pub, sub) =
//              peerFlow
//                .join(
//                  JarTreeStandaloneProtocol.Framing.Akka.reversed
//                )
//                .joinMat(
//                  Flow
//                    .fromSinkAndSourceMat(
//                      Sink.asPublisher[ByteString](false),
//                      Source.asSubscriber[ByteString]
//                    )(Keep.both)
//                )(Keep.right)
//                .run()
//
//            Observable
//              .fromReactivePublisher(pub)
//              .transform(
//                JarTreeStandaloneProtocol
//                  .Multiplex
//                  .connect(
//                    Seq(
//                      management,
//                      data
//                    )
//                  )
//              )
//              .subscribe(
//                Subscriber.fromReactiveSubscriber(
//                  sub,
//                  Cancelable.empty
//                )
//              )
//
//        })

//  def createManagement(
//    jarTree: JarTree,
//    socket: SimpleJarSocket[Service, JarTreeStandaloneContext]
//  ) = {
//    val jarCache = jarTree.cache
//    import boopickle.Default._
//    import toolbox8.akka.statemachine.ByteStringState._
//
//    val init = stateAsync(
//      fn = { bs =>
//        val vq = Unpickle[VerifyRequest].fromBytes(bs.asByteBuffer)
//
//        logger.info(s"verifying: ${vq.ids.mkString(", ")}")
//
//        val (missingId, missingIdx) =
//          vq
//            .ids
//            .zipWithIndex
//            .filter({
//              case (id, idx) if !jarCache.contains(id) => true
//              case _ => false
//            })
//            .unzip
//
//        val vro =
//          Observable(
//            ByteString.fromByteBuffer(
//              Pickle.intoBytes(
//                VerifyResponse(
//                  missingIdx
//                )
//              )
//            )
//          )
//
//        def plug = Task {
//
//        }
//
//        if (missingId.isEmpty) {
//          // plug now
//          ???
//
//        } else {
//          Task.now(
//            stateAsync(
//              vro,
//              { bs =>
//                val ph = Unpickle[PutHeader].fromBytes(bs.asByteBuffer)
//                missingId
//                  .zip(ph.sizes)
//                  .foldRight(ByteStringState)
//
//
//
//                ???
//              }
//
//            )
//
//          )
//        }
//
//      }
//    )
//
//    Management
//      .layer { o =>
//        o
//          .dump("mgmt")
//          .transform(init.transformer)
//      }
//
//  }




}

