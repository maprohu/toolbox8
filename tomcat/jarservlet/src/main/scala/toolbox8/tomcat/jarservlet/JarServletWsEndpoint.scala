package toolbox8.tomcat.jarservlet

import java.nio.ByteBuffer
import java.security.MessageDigest
import javax.websocket.MessageHandler.Partial
import javax.websocket._

import akka.http.scaladsl.model.ws.{BinaryMessage, Message}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{FileIO, Flow, Sink, Source}
import akka.util.ByteString
import toolbox8.tomcat.shared._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.collection.immutable._

class JarServletWsEndpoint(
  provider: PersistedProvider,
  global: Global
) extends Endpoint {

  override def onOpen(session: Session, config: EndpointConfig): Unit = {

    import global.materializer

    def flow() = {
      Source
        .queue[(ByteString, Boolean)](1, OverflowStrategy.backpressure)
        .splitAfter(_._2)
        .prefixAndTail(0)
        .map({
          case (_, source) =>
            BinaryMessage.Streamed(source.map(_._1))
        })
        .concatSubstreams
        .via(
          BinaryMessages.toStrictFlow
        )
        .map({ m =>
          import boopickle.Default._
          Unpickle[ClientToServer].fromBytes(m.data.asByteBuffer)
        })
        .splitAfter({ m =>
          m match {
            case (_:VerifyJars | _:UploadJarEnd) => true
            case _ => false
          }
        })
        .prefixAndTail(1)
        .flatMapConcat({
          case (Seq(item), source) =>
            item match {
              case v : VerifyJars =>
                source
                  .via(
                    Flow.fromSinkAndSource(
                      Sink.ignore,
                      Source.single(
                        JarsVerified(
                          v.coords.filterNot(provider.jarFileExists)
                        )
                      )
                    )
                  )

              case s : UploadJarStart =>

                source
                  .alsoTo(
                    Flow[ClientToServer]
                      .collect({
                        case p : UploadJarPiece =>
                          ByteString(p.data)
                      })
                      .to(
                        FileIO.toPath(
                          provider.jarPath(s.coords)
                        )
                      )
                  )
                  .statefulMapConcat({ () =>
                    val digest = MessageDigest.getInstance("MD5")

                    {
                      case p : UploadJarPiece =>
                        digest.digest(p.data)
                        Seq.empty
                      case p : UploadJarEnd =>
                        Seq(
                          JarUploaded(
                            s.coords,
                            success = p.md5 sameElements digest.digest()
                          )
                        )
                      case _ => ???
                    }
                  })

              case r : Run =>
                source
                  .map({
                    case w : Wrapped =>
                      ByteString(w.data)
                    case _ => ???
                  })
                  .via(
                    provider
                      .loadInstance[FlowProvider](
                        r.jars.map(jar => provider.jarPath(jar).toUri.toURL),
                        r.className
                      )
                      .apply(global, provider)
                  )
                  .map(bs => Wrapped(bs.toArray))

              case _ => ???
            }
        })
        .concatSubstreams
        .map({ m =>
          import boopickle.Default._
          BinaryMessage(
            ByteString(
              Pickle[ServerToClient](m).toByteBuffer
            )
          )
        })
        .flatMapConcat({ m =>
          m
            .dataStream
            .map(Some.apply)
            .concat(Source.single(None))
            .sliding(2)
            .map({
              case Seq(Some(msg), next) =>
                (msg, next.isEmpty)
              case _ => ???
            })

        })
        .to(
          Sink.foreach({
            case (bs, last) =>
              // TODO blocking thread pool
              session
                .getBasicRemote
                .sendBinary(bs.asByteBuffer, last)
          })
        )
        .run()
    }

    session.addMessageHandler(
      new Partial[ByteBuffer] {
        override def onMessage(partialMessage: ByteBuffer, last: Boolean): Unit = {
          Await.result(
            flow.offer((ByteString(partialMessage), last)),
            Duration.Inf
          )
        }
      }
    )

  }

}
