package toolbox8.tomcat.testapp

import javax.servlet.ServletContext

import toolbox8.tomcat.jarservlet.JarServlet.Plugged
import toolbox8.tomcat.jarservlet.{Global, JarServletProvider}

/**
  * Created by pappmar on 14/03/2017.
  */
class TestingPluggedProvider extends JarServletProvider {
  override def apply(servletContext: ServletContext, global: Global): Plugged = {
    Plugged(
      handler = { (req, res) =>
        val out = res.getWriter
        out.write("plugged")
        out.flush()
      }
    )
  }
}
