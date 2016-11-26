package toolbox8.jartree.streamapp

import java.io._
import java.net.Socket

import com.typesafe.scalalogging.StrictLogging
import org.apache.commons.io.IOUtils
import org.apache.commons.io.input.BoundedInputStream
import toolbox6.logging.LogTools
import toolbox8.jartree.common.JarKey

/**
  * Created by maprohu on 21-11-2016.
  */
class StreamAppThread[P <: Plugged](
  socket: Socket,
  id: Int,
  cache: JarCache,
  rootDir: File,
  ctx: RootContext[P],
  onStop: StreamAppThread[P] => Unit
) extends Thread with StrictLogging with LogTools {
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

              true

            case p : RunRequest[_, _, _] =>
              val pt = p.asInstanceOf[RunRequest[P, AnyRef, AnyRef]]
              logger.info("run request: {}", p)

              val result = try {
                logger.info("loading requestable instance")
                val r = cache.loadInstance(
                  pt.classLoaderConfig,
                  ctx.root.getClass.getClassLoader
                )

                logger.info("reading request input")

                val input = withInputClassLoader(r.getClass.getClassLoader) { () =>
                  dis
                    .readObject()
                    .asInstanceOf[RunRequestInput[AnyRef]]
                }

                logger.info("processing request")

                r.request(ctx.root, input.input)
              } catch {
                case ex : Throwable =>
                  ex
              }

              logger.info(s"sending response: ${result}")
              dos.writeObject(
                result
              )
              dos.flush()
              true

            case p : PutRoot =>
              logger.info("put root: {}", p)

              logger.info("loading new root instance")
              val r = cache.loadInstance(
                p.classLoaderConfig,
                getClass.getClassLoader
              )

              val oldRoot = ctx.synchronized {
                logger.info("pre unplugging old instance")
                val prev =
                   ctx.root.preUnplug

                logger.info("plugging new instance")
                val plugged =
                  r
                    .plug(
                      PlugParams(
                        prev,
                        cache,
                        rootDir
                      )
                    )
                    .asInstanceOf[P]

                val oldRoot = ctx.root
                ctx.root = plugged
                oldRoot
              }
              quietly {
                logger.info("post unplugging old instance")
                oldRoot.postUnplug
              }
              logger.info("saving root config")
              val fos = new ObjectOutputStream(new FileOutputStream(rootConfigFile))
              try {
                fos.writeObject(p.classLoaderConfig)
              } finally {
                fos.close()
              }

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

case class PutRoot(
  classLoaderConfig: ClassLoaderConfig[Root]
) extends Init

case class RunRequest[Ctx <: Plugged, In, Out](
  classLoaderConfig: ClassLoaderConfig[Requestable[Ctx, In, Out]]
) extends Init

case class RunRequestInput[In](
  input: In
)