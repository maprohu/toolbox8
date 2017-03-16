package toolbox8.tomcat.jarservlet

import javax.servlet.{ServletConfig, ServletContext}
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import com.typesafe.scalalogging.StrictLogging
import monix.execution.atomic.{Atomic, AtomicAny}
import toolbox8.tomcat.jarservlet.JarServlet.Plugged


class JarServlet(
  service: AtomicAny[Plugged]
) extends HttpServlet with StrictLogging {

  override def service(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    service.get.handler(req, resp)
  }
}

object JarServlet {

  case class Plugged(
    handler: Handler = { (_, _) => },
    control: Any = (),
    stop: () => Unit = () => ()
  )

  val DefaultPlugged = Plugged()

  type Handler = (HttpServletRequest, HttpServletResponse) => Unit

}

trait JarServletProvider {
  def apply(servletContext: ServletContext, global: Global) : Plugged
}
