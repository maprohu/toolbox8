package toolbox8.servlet31.runapi

import javax.servlet.http.HttpServlet

import jartree.{JarTreeRunner, RunRequest}
import toolbox8.servlet31.singleapi.Servlet31SingleHandler

/**
  * Created by pappmar on 30/08/2016.
  */
trait Servlet31Runner {

  def run(context: Servlet31Context) : Unit

}

trait Servlet31Context {

  def servlet() : HttpServlet

  def jarTree() : JarTreeRunner

  def setStartup(runRequest: RunRequest) : Unit

  def setHandler(handler: Option[Servlet31SingleHandler]) : Unit

}
