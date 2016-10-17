package toolbox8.jartree.standalone

import java.io.InputStream
import java.nio.ByteBuffer
import java.util

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source, Tcp}
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import monix.execution.Cancelable
import monix.reactive.Observable
import monix.reactive.observers.Subscriber
import toolbox6.jartree.impl.{JarCache, JarTreeBootstrap}
import toolbox8.jartree.protocol.JarTreeStandaloneProtocol
import toolbox8.jartree.protocol.JarTreeStandaloneProtocol.{Management, Multiplex}
import toolbox8.jartree.standaloneapi.{JarTreeStandaloneContext, Message, PeerInfo, Service}
import monix.execution.Scheduler.Implicits.global
import org.reactivestreams.Processor
import toolbox6.jartree.api.ClassRequest
import toolbox6.jartree.impl.JarTreeBootstrap.Config
import toolbox6.jartree.util.CaseJarKey
import toolbox6.jartree.wiring.PlugRequestImpl
import toolbox6.javaapi.{AsyncCallback, AsyncValue}
import toolbox6.javaimpl.JavaImpl
import toolbox6.statemachine.State
import toolbox8.jartree.protocol.JarTreeStandaloneProtocol.Management.{PutHeader, VerifyRequest, VerifyResponse}
import toolbox8.jartree.standaloneapi.Message.Header
import toolbox8.jartree.util.VoidService

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

/**
  * Created by martonpapp on 15/10/16.
  */
object JarTreeStandalone extends LazyLogging {

  def run(
    name: String,
    port: Int,
    version: Int = -1,
    embeddedJars: Seq[(CaseJarKey, () => InputStream)],
    initialStartup: PlugRequestImpl[Service, JarTreeStandaloneContext]
  ) = {
    val rt = JarTreeBootstrap
      .init[Service, JarTreeStandaloneContext](
        Config(
          contextProvider = jarTree => new JarTreeStandaloneContext {
            override def resolve[T](request: ClassRequest[T]): T = jarTree.resolve(request)
          }:JarTreeStandaloneContext,
          voidProcessor = VoidService,
          name = name,
          dataPath = s"/opt/${name}/data",
          version = version,
          embeddedJars = embeddedJars,
          initialStartup = initialStartup,
          runtimeVersion = JarTreeStandalone.getClass.getPackage.getImplementationVersion
        )
      )


    implicit val actorSystem = ActorSystem()
    implicit val materializer = ActorMaterializer(
      Some(
        ActorMaterializerSettings(actorSystem)
          .withSupervisionStrategy({ ex =>
            logger.error(ex.getMessage, ex)
            Supervision.Stop
          })
      )
    )

    Tcp()
      .bind(
        "0.0.0.0",
        port
      )
      .mapAsync(1)({ incoming =>
        logger.info("incoming: {}", incoming.remoteAddress)
        JavaImpl
          .wrap(
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
              rt.jarTree.cache
            )


            val data = Multiplex.Layer(
              headerCount = 0,
              flow = { o =>
                val out =
                  Observable
                    .fromReactivePublisher(
                      dataProc
                    )
                    .map({ m =>
                      Multiplex.Message(
                        header = m.header(),
                        data =
                          m
                            .data()
                            .foldLeft(
                              ByteString.empty
                            )({ (bs, bb) =>
                              bs ++ ByteString.fromByteBuffer(bb)
                            })
                      )
                    })

                o
                  .map({ m =>
                    new Message {
                      override def data(): util.Enumeration[ByteBuffer] = {
                        m
                          .data
                          .asByteBuffers
                          .iterator
                      }
                      override def header(): Header = m.header.toByte
                    }
                  })
                  .subscribe(
                    Subscriber
                      .fromReactiveSubscriber(
                        dataProc,
                        Cancelable.empty
                      )
                  )

                out
              }
            )


            val (pub, sub) =
              peerFlow
                .join(
                  JarTreeStandaloneProtocol.Framing.Akka.reversed
                )
                .joinMat(
                  Flow
                    .fromSinkAndSourceMat(
                      Sink.asPublisher[ByteString](false),
                      Source.asSubscriber[ByteString]
                    )(Keep.both)
                )(Keep.right)
                .run()

            Observable
              .fromReactivePublisher(pub)
              .transform(
                JarTreeStandaloneProtocol
                  .Multiplex
                  .connect(
                    Seq(
                      management,
                      data
                    )
                  )
              )
              .subscribe(
                Subscriber.fromReactiveSubscriber(
                  sub,
                  Cancelable.empty
                )
              )

        })
      )(Keep.both)
      .run()


  }

  def createManagement(
    jarCache: JarCache

  ) = {
    import boopickle.Default._
    def state(
      out: Observable[ByteString] = Observable.empty,
      fn: ByteString => State[ByteString, ByteString]
    ) = State(out, fn)

    val init = state(
      fn = { bs =>
        val vq = Unpickle[VerifyRequest].fromBytes(bs.asByteBuffer)

        logger.info(s"verifying: ${vq.ids.mkString(", ")}")

        val (missingId, missingIdx) =
          vq
            .ids
            .zipWithIndex
            .filter({
              case (id, idx) if !jarCache.contains(id) => true
              case _ => false
            })
            .unzip

        state(
          Observable(
            ByteString.fromByteBuffer(
              Pickle.intoBytes(
                VerifyResponse(
                  missingIdx
                )
              )
            )
          ),
          { bs =>
            val ph = Unpickle[PutHeader].fromBytes(bs.asByteBuffer)
            missingId
              .zip(ph.sizes)



            ???
          }

        )
      }
    )

    Management
      .layer { o =>
        o
          .dump("mgmt")
          .transform(init.transformer)
      }

  }




}

