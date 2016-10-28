package toolbox8.jartree.app

import ch.qos.logback.classic.LoggerContext
import com.typesafe.scalalogging.LazyLogging
import org.slf4j.LoggerFactory
import toolbox8.jartree.standalone.JarTreeStandalone

import scala.collection.immutable._

/**
  * Created by martonpapp on 20/10/16.
  */
object JarTreeMain extends LazyLogging {

  def main(args: Array[String]): Unit = {
    val lcf = new LogbackConfigurator()
    val lc = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
    lcf.reset(lc)
    val name = if (args.length >= 1) {
      args(0)
    } else {
      lcf.configStdout(lc)
      lcf.configDebug(lc, true)
      "jartree"
    }
    lcf.configure(
      name,
      lc
    )

    import monix.execution.Scheduler.Implicits.global
    JarTreeStandalone
      .run(
        name,
        embeddedJars = Seq(),
        initialStartup = None,
        runtimeVersion = JarTreeMain.getClass.getPackage.getImplementationVersion
      )
  }

}
