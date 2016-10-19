package toolbox8.jartree.testing

import java.io.FileInputStream

import com.typesafe.scalalogging.LazyLogging
import toolbox6.jartree.packaging.JarTreePackaging
import toolbox6.jartree.packaging.JarTreePackaging.RunHierarchy
import toolbox6.jartree.util.{CaseJarKey, ClassRequestImpl}
import toolbox6.jartree.wiring.PlugRequestImpl
import toolbox8.jartree.echo.EchoPlugger
import toolbox8.jartree.standalone.JarTreeStandalone
import toolbox8.jartree.standaloneapi.{JarTreeStandaloneContext, Service}
import toolbox8.modules.{JarTree8Modules, Toolbox8Modules}

import monix.execution.Scheduler.Implicits.global
import ammonite.ops._

/**
  * Created by martonpapp on 15/10/16.
  */
object RunJarTreeStandalone extends LazyLogging {

  val Name = "jtsttesting"

  def main(args: Array[String]): Unit = {
    rm(root / 'opt / Name)

    val runHierarchy = RunHierarchy(
      JarTree8Modules.Echo,
      runClassName = classOf[EchoPlugger].getName
    )
    val target = JarTree8Modules.Standalone

    val rmh =
      runHierarchy
        .forTarget(
          JarTreePackaging.target(
            target
          )
        )

    val jars =
      JarTreePackaging
        .resolverJarsFile(rmh)
        .map({
          case (id, file) =>
            (CaseJarKey(id), () => new FileInputStream(file))
        })

    JarTreeStandalone.run(
      name = Name,
      port = 9978,
      embeddedJars = jars,
      initialStartup = PlugRequestImpl[Service, JarTreeStandalone.CTX](
        ClassRequestImpl(
          rmh.classLoader,
          rmh.runClassName
        ),
        Array.emptyByteArray
      )
    )

//    bind.onComplete(o => logger.info(o.toString))



  }

}
