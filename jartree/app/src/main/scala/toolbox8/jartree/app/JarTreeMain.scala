package toolbox8.jartree.app

import ch.qos.logback.classic.LoggerContext
import org.slf4j.LoggerFactory
import toolbox8.jartree.standalone.JarTreeStandalone

/**
  * Created by martonpapp on 20/10/16.
  */
object JarTreeMain {

  def main(args: Array[String]): Unit = {
    val name = if (args.length >= 1) args(0) else "jartree"

    new LogbackConfigurator().configure(
      name,
      LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
    )

    import monix.execution.Scheduler.Implicits.global
    JarTreeStandalone
      .run(
        name
      )
  }

}
