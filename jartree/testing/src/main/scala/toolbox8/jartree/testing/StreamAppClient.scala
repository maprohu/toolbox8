package toolbox8.jartree.testing

import java.io._
import java.net.Socket

import com.typesafe.scalalogging.LazyLogging
import mvnmod.builder.{Module, ModulePath}
import org.apache.commons.io.IOUtils
import toolbox8.jartree.client.JarResolver
import toolbox8.jartree.common.JarKey
import toolbox8.jartree.streamapp._
import toolbox8.modules.JarTree8Modules
import toolbox8.jartree.common.JarTreeApp.Config
import toolbox8.jartree.requests.{PluggedRequestable, PutRootStopFirstRequest}

/**
  * Created by maprohu on 21-11-2016.
  */
object StreamAppClient extends LazyLogging {



  case class StreamAppConnection(
    socket: Socket,
    os: OutputStream,
    dos: ObjectOutputStream,
    is: InputStream,
    dis: ObjectInputStream
  ) {
    def close() = {
      dos.close()
      os.close()
      dis.close()
      is.close()
      socket.close()
    }
  }


  def open(
    target: Config
  ) = {

    logger.info(s"connecting to: ${target.host}:${target.servicePort}")

    val socket = new Socket(
      target.host,
      target.servicePort
    )

    val os = socket.getOutputStream
    val dos = new ObjectOutputStream(os)
    val is = socket.getInputStream
    val dis = new ObjectInputStream(is)

    StreamAppConnection(
      socket,
      os,
      dos,
      is,
      dis
    )
  }

  val StreamAppPath =
    ModulePath(
      JarTree8Modules.StreamApp,
      None
    )

  def putCache(
    module: Module,
    connection: StreamAppConnection,
    targetPath : ModulePath = StreamAppPath
  ) = {
    putCacheMulti(
      Seq(module),
      connection,
      targetPath
    )
  }
  def putCacheMulti(
    module: Seq[Module],
    connection: StreamAppConnection,
    targetPath : ModulePath = StreamAppPath
  ) = {

    import connection._

    val jars =
      module
        .flatMap({ m =>
          m
            .forTarget(
              targetPath
            )
            .classPath
        })
        .distinct

    println(
      s"""putting:
        |${jars.mkString("\n")}
      """.stripMargin
    )

    val vreq = VerifyCacheRequest(
      jars =
        jars
          .map({ f =>
            JarResolver.resolveHash(
              JarKey(
                f.groupId,
                f.artifactId,
                f.version
              )
            )
          })
          .toVector
    )
    println(vreq)
    dos.writeObject(
      vreq
    )
    dos.flush()

    val r = dis.readObject().asInstanceOf[VerifyCacheResponse]
    println(r)

    r
      .missing
      .foreach({ k =>
        println(k)
        val f = JarResolver.resolveFile(k)
        dos.writeObject(
          PutCacheRequest(
            k,
            f.length()
          )
        )
        dos.flush()
        val fis = new FileInputStream(f)
        IOUtils.copy(
          fis,
          os
        )
        fis.close()

        logger.info("waiting for confirmation")
        require(dis.readObject() == Done)
      })


    vreq.jars
  }

  def plug(
    module: Module,
    rootClassName: String,
    target: Config,
    targetPath: ModulePath = StreamAppPath
  ) : Unit = {
    plugMulti(
      Seq(module),
      rootClassName,
      target,
      targetPath
    )
  }

  def plugMulti(
    module: Seq[Module],
    rootClassName: String,
    target: Config,
    targetPath: ModulePath = StreamAppPath
  ) : Unit = {

    request(
      JarTree8Modules.Requests,
      classOf[PutRootStopFirstRequest].getName,
      { c =>
        val jars = putCacheMulti(
          module,
          c,
          targetPath
        )

        { (is, os) =>
          val dos = new ObjectOutputStream(os)
          dos.writeObject(
            ClassLoaderConfig(
              jars,
              rootClassName
            )
          )
          dos.flush()
        }
      },
      target,
      targetPath
    )
  }

  type RequestHandler[T] = (InputStream, OutputStream) => T
  type RequestConnectionHandler[T] = StreamAppConnection => RequestHandler[T]

  def requestPlugged[T](
    module: Module,
    requestableClassName: String,
    handler: RequestHandler[T],
    target: Config,
    moduleTargetPath: ModulePath,
    targetPath: ModulePath = StreamAppPath
  ) : T = {
    request(
      JarTree8Modules.Requests,
      classOf[PluggedRequestable].getName,
      { c =>
        val jars = putCache(module, c, moduleTargetPath)
        import c._

        { (is, os) =>
          val clc =
            ClassLoaderConfig[Requestable](
              jars,
              requestableClassName
            )
          dos.writeObject(clc)
          dos.flush()

          handler(is, os)
        }
      },
      target,
      targetPath
    )

  }

  def request[T](
    module: Module,
    requestableClassName: String,
    handler: RequestConnectionHandler[T],
    target: Config,
    targetPath: ModulePath = StreamAppPath
  ) : T = {
    val c = open(target)
    try {
      val jars = putCache(module, c, targetPath)

      val h = handler(c)

      import c._

      val preq =
        RunRequest(
          ClassLoaderConfig[Requestable](
            jars,
            requestableClassName
          )
        )
      println(preq)
      dos.writeObject(preq)
      dos.flush()

      val result = h(is, os)

      logger.info("waiting for confirmation")
      require(dis.readObject() == Done)

      result
    } finally {
      c.close()
    }

  }


}
