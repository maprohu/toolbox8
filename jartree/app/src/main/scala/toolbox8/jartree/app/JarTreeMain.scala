package toolbox8.jartree.app

import ch.qos.logback.classic.LoggerContext
import com.typesafe.scalalogging.LazyLogging
import org.slf4j.LoggerFactory

import scala.collection.immutable._

/**
  * Created by martonpapp on 20/10/16.
  */
object JarTreeMain extends LazyLogging {

  def configureLogging(
    name: String,
    debug: Boolean
  ) = {
    val lcf = new LogbackConfigurator()
    val lc = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
    lcf.reset(lc)
    lcf.configDebug(lc, debug)
    lcf.configure(
      name,
      lc
    )
    if (debug) {
      lcf.configStdout(lc)
    }

    lcf.logFile(name)
  }

  def initLogging(args: Array[String]) = {
    val (log, name) = if (args.length >= 1) {
      val n = args(0)
      (configureLogging(n, false), n)
    } else {
      val n = "jartree"
      (configureLogging(n, true), n)
    }
    logger.info("logfile: {}", log)
    logger.debug("debug level enabled")
    (log, name)
  }

//  def main(args: Array[String]): Unit = {
//    val (log, name) = initLogging(args)
//
//    import monix.execution.Scheduler.Implicits.global
//    JarTreeStandalone
//      .run(
//        name,
//        embeddedJars = Seq(),
//        initialStartup = None,
//        runtimeVersion = JarTreeMain.getClass.getPackage.getImplementationVersion,
//        logFile = Some(log.toPath)
//      )
//  }

}
