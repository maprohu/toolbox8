package toolbox8.jartree.testing

import java.io.FileInputStream

import com.typesafe.scalalogging.LazyLogging
import toolbox6.jartree.packaging.JarTreePackaging
import toolbox8.jartree.echo.EchoPlugger
import toolbox8.jartree.standalone.{JarTreeStandalone, ScalaJarTreeStandaloneContext}
import toolbox8.jartree.standaloneapi.{JarTreeStandaloneContext, Service}
import toolbox8.modules.{JarTree8Modules, Toolbox8Modules}
import monix.execution.Scheduler.Implicits.global
import ammonite.ops._
import maven.modules.builder.ModulePath
import toolbox6.jartree.api.{ClassRequest, JarKey, PlugRequest}

/**
  * Created by martonpapp on 15/10/16.
  */
object RunJarTreeStandalone extends LazyLogging {

  val Name = "jtsttesting"

  def main(args: Array[String]): Unit = {
    rm(root / 'opt / Name)

    val module = JarTree8Modules.Echo
    val runClassName = classOf[EchoPlugger].getName
    val target = JarTree8Modules.Standalone

    val rmh =
      module
        .forTarget(
          ModulePath(
            target,
            None
          )
        )
        .classPath

    val jars =
      JarTreePackaging
        .resolverJarsFile(rmh)
        .map({
          case (id, file) =>
            (JarKey(id), () => new FileInputStream(file))
        })

    JarTreeStandalone.run(
      name = Name,
      port = 9978,
      embeddedJars = jars,
      initialStartup =
        Some(
          PlugRequest[Service, JarTreeStandaloneContext](
            ClassRequest(
              rmh.map(JarTreePackaging.getId),
              runClassName
            )
          )
        ),
      runtimeVersion = "testing"
    )


//    bind.onComplete(o => logger.info(o.toString))



  }

}
