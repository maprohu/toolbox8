package toolbox8.servlet31.webapp

import java.io.File
import javax.servlet.ServletConfig
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import jartree.impl.JarTree
import jartree.impl.servlet.PersistedRunner
import jartree.{JarTreeRunner, RunRequest}
import toolbox8.servlet31.runapi.{Servlet31Context, Servlet31Runner}
import toolbox8.servlet31.singleapi.Servlet31SingleHandler

import scala.concurrent.ExecutionContext

/**
  * Created by pappmar on 30/08/2016.
  */
class Servlet31PersistedWebappServlet(
  workDir: File,
  initializer: JarTree => Unit,
  classLoader: ClassLoader,
  initialRun: RunRequest
)(implicit
  executionContext: ExecutionContext
) extends Servlet31WebappServlet { self =>

  val runner = new PersistedRunner[Servlet31Runner](
    workDir,
    initializer,
    classLoader,
    initialRun
  )


  val context = new Servlet31Context {
    override def servlet(): HttpServlet = self

    override def jarTree(): JarTree = runner.jarTree

    override def setStartup(runRequest: RunRequest): Unit = runner.StateIO.writeState(runRequest)

    override def setHandler(handler: Option[Servlet31SingleHandler]): Unit = singleApi.set(handler)
  }

  override def init(config: ServletConfig): Unit = {
    super.init(config)

    start()
  }

  def start() = {
    runner.start(_.run(context))
  }

  override def service(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    Option(req.getPathInfo) match {
      case Some("/_admin/reset") =>
        start()
      case _ =>
        super.service(req, resp)
    }
  }
}

