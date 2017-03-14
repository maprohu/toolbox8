package toolbox8.tomcat.jarservlet

import javax.servlet.annotation.WebServlet
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

@WebServlet(
  name="jarservlet",
  urlPatterns = Array("/*")
)
class JarServlet extends HttpServlet {
  override def service(req: HttpServletRequest, resp: HttpServletResponse): Unit = super.service(req, resp)
}

object JarServlet {

  type Handler = (HttpServletRequest, HttpServletResponse) => Unit

  val DefaultHandler : Handler = { (_, _) => }

  val service = Atomic(DefaultHandler)

}
