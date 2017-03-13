package toolbox8.tomcat.jarservlet

import javax.servlet.annotation.WebServlet
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

@WebServlet(
  name="jarservlet",
  urlPatterns = Array("/*")
)
class JarServlet extends HttpServlet {
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val out = resp.getWriter
    out.println("hello")
    out.flush()
  }
}
