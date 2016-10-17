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

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by martonpapp on 15/10/16.
  */
object RunJarTreeStandalone extends LazyLogging {

  def main(args: Array[String]): Unit = {
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

    val (bind, handle) = JarTreeStandalone.run(
      name = "jtsttesting",
      port = 9978,
      embeddedJars = jars,
      initialStartup = PlugRequestImpl[Service, JarTreeStandaloneContext](
        ClassRequestImpl(
          rmh.classLoader,
          rmh.runClassName
        ),
        Array.emptyByteArray
      )
    )

    bind.onComplete(o => logger.info(o.toString))



  }

}
