package toolbox8.jartree.streamapp

import java.io._
import java.net.Socket

import com.typesafe.scalalogging.StrictLogging
import org.apache.commons.io.IOUtils
import org.apache.commons.io.input.BoundedInputStream
import toolbox6.logging.LogTools
import toolbox8.jartree.common.JarKey
import toolbox8.jartree.requestapi.RequestMarker

/**
  * Created by maprohu on 21-11-2016.
  */
class StreamAppThread(
  socket: Socket,
  id: Int,
  rootDir: File,
  ctx: RootContext,
  onStop: StreamAppThread => Unit
) extends Thread with StrictLogging with LogTools {
  import ctx.cache

  setName(s"client-${id}")
  val rootConfigFile = new File(rootDir, ClassLoaderConfig.ClassLoaderConfigFileName)

  override def run(): Unit = {

    quietly {
      val is = socket.getInputStream
      val os = socket.getOutputStream

      try {
        var inputClassLoader = getClass.getClassLoader
        val dis = new ObjectInputStream(is) {
          override def resolveClass(desc: ObjectStreamClass): Class[_] = {
            try {
              Class.forName(desc.getName, false, inputClassLoader)
            } catch {
              case _ : ClassNotFoundException =>
                super.resolveClass(desc)
            }
          }
        }
        def withInputClassLoader[T](cl: ClassLoader)(fn: () => T) = {
          val saved = inputClassLoader
          inputClassLoader = cl
          try {
            fn()
          } finally {
            inputClassLoader = saved
          }
        }
        val dos = new ObjectOutputStream(os)

        while ({
          val init = try {
            dis.readObject().asInstanceOf[Init]
          } catch {
            case _ : EOFException =>
              logger.info("connection closed")
              End
          }

          init match {
            case v : VerifyCacheRequest =>
              logger.info("verifying cache: {}", v)
              val r =
                VerifyCacheResponse(
                  missing =
                    v
                      .jars
                      .filterNot({ k =>
                        cache.get(k).exists()
                      })
                )
              logger.info("sending response: {}", r)
              dos.writeObject(
                r
              )
              dos.flush()
              true

            case p : PutCacheRequest =>
              logger.info("put cache: {}", p)
              val f = cache.get(p.jarKey)
              require(f.createNewFile())
              val fos = new FileOutputStream(f)
              IOUtils.copy(
                new BoundedInputStream(is, p.size),
                fos
              )
              fos.close()

              dos.writeObject(Done)
              dos.flush()

              true

            case p : RunMarked =>
              logger.info("run marked: {}", p)

              val result = try {
                logger.info("reading request input")
                val root = ctx.holder.get

                val cl = cache.classLoader(
                  p.jars,
                  root.classLoader
                )

                val input = withInputClassLoader(cl) { () =>
                  dis
                    .readObject()
                    .asInstanceOf[RunMarkedRequest[AnyRef, AnyRef]]
                }

                logger.info("processing request")
                root.plugged.marked(
                  input.marker,
                  input.input
                )
              } catch {
                case ex : Throwable =>
                  logger.error(ex.getMessage, ex)
                  ex
              }

              logger.info(s"sending response: ${result}")
              dos.writeObject(
                result
              )
              dos.flush()
              true

            case pt : RunRequest =>
              logger.info("run request: {}", pt)

              try {
                logger.info("loading requestable instance")
                val r = cache.loadInstance(
                  pt.classLoaderConfig,
                  ctx.parent
                )

                logger.info("processing request")

                r.request(
                  ctx,
                  is,
                  os
                )

                logger.info(s"request processing complete")
              } catch {
                case ex : Throwable =>
                  dos.writeObject(
                    ex
                  )
                  dos.flush()
                  logger.warn("request processing failed", ex)
              }

              dos.writeObject(Done)
              dos.flush()

              true

            case End =>
              false
          }
        }) {}
      } finally {
        quietly { is.close() }
        quietly { os.close() }
        quietly { socket.close() }
        quietly { onStop(this) }
      }
    }

  }

}

sealed trait Init
case object End extends Init

@SerialVersionUID(1)
case object Done

case class VerifyCacheRequest(
  jars: Vector[JarKey]
) extends Init

case class VerifyCacheResponse(
  missing: Vector[JarKey]
)

case class PutCacheRequest(
  jarKey: JarKey,
  size: Long
) extends Init

//case class PutRoot(
//  classLoaderConfig: ClassLoaderConfig[Root]
//) extends Init

case class RunMarked(
  jars: Vector[JarKey]
) extends Init

case class RunRequest(
  classLoaderConfig: ClassLoaderConfig[Requestable]
) extends Init

case class RunMarkedRequest[In, Out](
  marker: RequestMarker[In, Out],
  input: In
)

//case class RunRequestInput[In](
//  input: In
//)