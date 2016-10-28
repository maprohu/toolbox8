package toolbox8.jartree.app

import ch.qos.logback.classic.LoggerContext
import org.slf4j.LoggerFactory
import toolbox8.jartree.standalone.JarTreeStandalone
import scala.collection.immutable._

/**
  * Created by martonpapp on 20/10/16.
  */
object JarTreeMain {

  def main(args: Array[String]): Unit = {
    val name = if (args.length >= 1) args(0) else "jartree"

    val lcf = new LogbackConfigurator()
    val lc =
      LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
    lcf.configure(
      name,
      lc
    )
    lcf.configStdout(lc)

    import monix.execution.Scheduler.Implicits.global
    JarTreeStandalone
      .run(
        name,
        embeddedJars = Seq(),
        initialStartup = None

      )
  }

}
