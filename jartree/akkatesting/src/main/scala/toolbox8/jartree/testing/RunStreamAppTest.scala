package toolbox8.jartree.testing

import java.io.{FileInputStream, ObjectInput, ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import mvnmod.builder.ModulePath
import org.apache.commons.io.IOUtils
import toolbox8.jartree.akka.PluggableServiceActor.PlugRequest
import toolbox8.jartree.client.JarResolver
import toolbox8.jartree.common.JarKey
import toolbox8.jartree.streamapp._
import toolbox8.modules.JarTree8Modules

/**
  * Created by maprohu on 21-11-2016.
  */
object RunStreamAppTest {



  def main(args: Array[String]): Unit = {

    val socket = new Socket(
      InetAddress.getLocalHost,
      StreamAppMain.DefaultPort
    )

    val jars =
      JarTree8Modules
        .Testing
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
          classOf[TestingRoot].getName
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
