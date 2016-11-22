package toolbox8.jartree.testing

import java.io.{FileInputStream, ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import mvnmod.builder.{Module, ModulePath}
import org.apache.commons.io.IOUtils
import toolbox8.jartree.client.JarResolver
import toolbox8.jartree.common.JarKey
import toolbox8.jartree.streamapp._
import toolbox8.modules.JarTree8Modules
import toolbox8.rpi.installer.RpiInstaller

/**
  * Created by maprohu on 21-11-2016.
  */
object StreamAppPlugger {



  def run(
    module: Module,
    rootClassName: String,
    target: RpiInstaller.Config
  ) = {

    val socket = new Socket(
      target.host,
      target.servicePort
    )

    val jars =
      module
        .forTarget(
          ModulePath(
            JarTree8Modules.StreamApp,
            None
          )
        )
        .classPath

    val os = socket.getOutputStream
    val dos = new ObjectOutputStream(os)
    val is = socket.getInputStream
    val dis = new ObjectInputStream(is)

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

    val preq =
      PutRoot(
        ClassLoaderConfig[Root](
          vreq.jars,
          rootClassName
        )
      )
    println(preq)
    dos.writeObject(preq)
    dos.flush()

    dos.close()
    os.close()
    socket.close()



  }

}
