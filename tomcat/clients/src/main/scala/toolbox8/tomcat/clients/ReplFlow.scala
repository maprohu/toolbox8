package toolbox8.tomcat.clients

import java.io._

import akka.stream.scaladsl.{Flow, StreamConverters}
import akka.util.ByteString
import com.typesafe.scalalogging.StrictLogging
import toolbox8.tomcat.jarservlet.{FlowProvider, Global, PersistedProvider}
import toolbox8.tomcat.shared.JarCoords

import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter.ILoop
import scala.concurrent.duration._

/**
  * Created by pappmar on 16/03/2017.
  */
class ReplFlow extends FlowProvider with StrictLogging {
  override def apply(global: Global, persisted: PersistedProvider): Flow[ByteString, ByteString, _] = {

    Flow
      .fromSinkAndSourceMat(
        StreamConverters.asInputStream(1.day),
        StreamConverters.asOutputStream(1.day)
      )({ (is, os) =>

        new Thread() {
          override def run(): Unit = {
            val dis = new ObjectInputStream(is)
            val keys = dis.readObject().asInstanceOf[Seq[JarCoords]]

            val repl = new ILoop(
              new BufferedReader(new InputStreamReader(is)),
              new PrintWriter(os)
            ) {
              override def createInterpreter(): Unit = {
                super.createInterpreter()
                intp.bind("global", global)
                intp.bind("persisted", persisted)
              }
            }

            val settings = new Settings()

            settings.classpath.value =
              keys
                .map({ jar =>
                  persisted
                    .jarPath(jar)
                    .toString
                })
                .mkString(File.pathSeparator)

            logger.info(s"classpath: ${settings.classpath.value}")

            repl.process(settings)

            logger.info("repl session finished")
          }
        }.start()

      })

  }
}
