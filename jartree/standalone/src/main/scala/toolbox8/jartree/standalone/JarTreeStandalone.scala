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
import toolbox8.jartree.standaloneapi.{JarTreeStandaloneContext, PeerInfo, Protocol, Service}
import org.reactivestreams.Processor
import toolbox6.jartree.api._
import toolbox6.jartree.impl.JarTreeBootstrap.Config
import toolbox6.jartree.util.JarTreeTools
import toolbox6.jartree.wiring.SimpleJarSocket
import toolbox6.statemachine.State
import toolbox8.akka.statemachine.AkkaStreamCoding
import toolbox8.akka.statemachine.AkkaStreamCoding.{Data, StateMachine}
import toolbox8.akka.statemachine.AkkaStreamCoding.StateMachine.{StateOut, Transition}
import toolbox8.akka.stream.AkkaStreamTools
import toolbox8.jartree.protocol.JarTreeStandaloneProtocol.Management
import toolbox8.jartree.protocol.JarTreeStandaloneProtocol.Management._
import toolbox8.jartree.util.VoidService

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.collection.immutable._

/**
  * Created by martonpapp on 15/10/16.
  */


object JarTreeStandalone extends LazyLogging {


  def run(
    name: String,
    port: Int = Protocol.DefaultPort,
    version: Int = -1,
    embeddedJars: Seq[(JarKey, () => InputStream)],
    initialStartup: Option[PlugRequest[Service, JarTreeStandaloneContext]],
    runtimeVersion: String
  )(implicit
    scheduler: Scheduler
  ) = {
    val cmps = AkkaStreamTools.bootstrap()


    val rt = JarTreeBootstrap
      .init[Service, JarTreeStandaloneContext](
      Config[Service, JarTreeStandaloneContext](
        { (jarTree, ctx) =>
          new JarTreeStandaloneContext {
            override def resolve(request: JarSeq) : Future[ClassLoader] = jarTree.resolve(request)
            override implicit val actorSystem: ActorSystem = cmps.actorSystem
            override implicit val materializer: Materializer = cmps.materializer
            override def jarTreeContext: JarTreeContext = ctx
          }
        },
        voidProcessor = VoidService,
        name = name,
        dataPath = s"/opt/${name}/data",
        version = version,
        embeddedJars,
        initialStartup,
        closer = _.close()
      )
    )

    import cmps.{dispatcher => _, _}

    Tcp()
      .bind(
        "0.0.0.0",
        port
      )
      .mapAsync(1)({ incoming =>
        logger.info("incoming: {}", incoming.remoteAddress)
        rt
          .processorSocket
          .get()
          .apply(
            PeerInfo(
              incoming.remoteAddress
            )
          )
          .map({ p =>
            (incoming.flow, p)
          })
      })
      .toMat(
        Sink.foreach({
          case (peerFlow, dataProc) =>
            val management = new Running(
              rt.jarTree,
              rt.jarCache,
              rt.processorSocket,
              runtimeVersion
            ).createManagement

            peerFlow
              .join(AkkaStreamCoding.framing.reversed)
              .join(
                AkkaStreamCoding
                  .Multiplex
                  .flow(
                    management,
                    dataProc
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

  case class Running(
    jarTree: JarTree,
    jarCache: JarCache,
    socket: SimpleJarSocket[Service, JarTreeStandaloneContext],
    runtimeVersion: String
  )(implicit
    materializer: Materializer
  ) {

    import AkkaStreamCoding.StateMachine.State
    import boopickle.Default._
    import AkkaStreamCoding.Implicits._
    import materializer.executionContext

    def createManagement: Flow[ByteString, ByteString, Any] = {
      AkkaStreamCoding
        .Terminal
        .bidi
        .join(
          AkkaStreamCoding
            .StateMachine
            .flow(
              State(next = Start)
            )
        )

    }

    val Start: Transition = {
      { data =>
        AkkaStreamCoding
          .unpickle[Starter](data)
          .flatMap({
            case vq: VerifyRequest =>
              logger.info(s"verifying jars: ${vq.ids.mkString(", ")}")
              val (ids, idxs) = vq
                .ids
                .zipWithIndex
                .filter({
                  case (id, idx) if !jarCache.contains(id) => true
                  case _ => false
                })
                .unzip

              logger.info(s"missing jars: ${ids.mkString(", ")}")

              Future.successful(
                verifyRespone(
                  idxs,
                  ids
                )
              )
            case Query =>
              Future.successful(
                State(
                  out = Source.single(
                    AkkaStreamCoding.pickle(
                      QueryResponse(
                        socket
                          .query()
                          .map({ p =>
                            p.request
                          }),
                        runtimeVersion
                      )
                    )
                  ),
                  next = _ => {
                    Future.successful(StateMachine.End)
                  }
                )
              )
            case p: Plug =>
              performPlug(p)
          })
      }
    }

    def verifyRespone(
      idxs: Seq[Int],
      ids: Seq[String]
    ): State = {
      import materializer.executionContext

      AkkaStreamCoding
        .StateMachine
        .sequenceInAndState(
          out = Source.single(
            AkkaStreamCoding.pickle(
              VerifyResponse(
                missing = idxs
              )
            )
          ),
          steps =
            ids
              .map({ id => { data: Data =>
                logger.info(s"receiving: ${id}")
                val putOpt = jarCache.putAsync(id)

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
          andThen = { () =>
            logger.info("all files received")
            State(
              out = Source.single(
                AkkaStreamCoding.pickle(Done)
              ),
              next = Start
            )
          }
        )

    }

    def performPlug(
      plug: Plug
    ): Future[State] = {
      import materializer.executionContext
      logger.info(s"plugging: ${plug}")

      for {
        inst <- {
          JarTreeTools
            .resolve(
              jarTree,
              plug.classRequest
            )
        }
        _ = logger.info(s"plugger resolved: ${inst}")
        _ <- {
          socket.plug(
            PlugRequest(
              plug.classRequest
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
  }



}

