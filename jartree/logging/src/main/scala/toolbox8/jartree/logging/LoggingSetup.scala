package toolbox8.jartree.logging

import ch.qos.logback.classic.LoggerContext
import com.typesafe.scalalogging.LazyLogging
import org.slf4j.LoggerFactory

/**
  * Created by martonpapp on 20/10/16.
  */
object LoggingSetup extends LazyLogging {

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


}
