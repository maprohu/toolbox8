package toolbox8.tomcat.jarservlet

import java.util.ServiceLoader
import javax.servlet.{ServletContextEvent, ServletContextListener}
import javax.servlet.annotation.WebListener
import javax.websocket.server.ServerEndpointConfig.Configurator
import javax.websocket.server.{ServerContainer, ServerEndpointConfig}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging
import monix.execution.atomic.Atomic

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by pappmar on 14/03/2017.
  */
@WebListener
class Startup extends ServletContextListener with StrictLogging {

  val global = Global.Instance

  override def contextDestroyed(sce: ServletContextEvent): Unit = {
    global.destroy()
  }

  override def contextInitialized(sce: ServletContextEvent): Unit = {
    import global._

    val provider = ServiceLoader.load(classOf[JarServletProvider])

    import scala.collection.JavaConverters._
    provider
      .iterator()
      .asScala
      .toStream
      .headOption
      .foreach { p =>
        logger.info(s"setting plugged: ${p}")
        service.set(p(sce.getServletContext, global))
      }


    val ctx = sce.getServletContext

    val reg = ctx.addServlet(
      "jarservlet",
      servlet
    )
    reg.addMapping("/*")
    reg.setLoadOnStartup(1)


  }

}

class Global {
  val service = Atomic(JarServlet.DefaultPlugged)

  val servlet = new JarServlet(service)

  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()

  def destroy(): Unit = {
    service
      .transform({ p =>
        p.stop()

        JarServlet.DefaultPlugged
      })

    Await.result(
      actorSystem.terminate(),
      15.seconds
    )
  }

}

object Global {
  val Instance = new Global
}

