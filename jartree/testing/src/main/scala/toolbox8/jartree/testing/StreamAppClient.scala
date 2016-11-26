package toolbox8.jartree.testing

import java.io._
import java.net.Socket

import mvnmod.builder.{Module, ModulePath}
import org.apache.commons.io.IOUtils
import toolbox8.jartree.client.JarResolver
import toolbox8.jartree.common.JarKey
import toolbox8.jartree.streamapp._
import toolbox8.modules.JarTree8Modules
import toolbox8.jartree.common.JarTreeApp.Config

/**
  * Created by maprohu on 21-11-2016.
  */
object StreamAppClient {



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

  def putCache(
    module: Module,
    connection: StreamAppConnection
  ) = {

    import connection._

    val jars =
      module
        .forTarget(
          ModulePath(
            JarTree8Modules.StreamApp,
            None
          )
        )
        .classPath

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
      })

    vreq.jars
  }

  def plug(
    module: Module,
    rootClassName: String,
    target: Config
  ) = {
    val c = open(target)
    val jars = putCache(module, c)
    import c._


    val preq =
      PutRoot(
        ClassLoaderConfig[Root](
          jars,
          rootClassName
        )
      )
    println(preq)
    dos.writeObject(preq)
    dos.flush()

    c.close()
  }

  def request[In, Out](
    module: Module,
    requestableClassName: String,
    inputParam: In,
    target: Config
  ) : Out = {
    val c = open(target)
    try {
      val jars = putCache(module, c)
      import c._

      val preq =
        RunRequest(
          ClassLoaderConfig[Requestable[Plugged, In, Out]](
            jars,
            requestableClassName
          )
        )
      println(preq)
      dos.writeObject(preq)
      dos.writeObject(RunRequestInput(inputParam))
      dos.flush()

      val result =
        dis
          .readObject()
          .asInstanceOf[Out]

      result
    } finally {
      c.close()
    }

  }

}
