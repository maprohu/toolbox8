package toolbox8.servlet31.singleapi

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

/**
  * Created by pappmar on 30/08/2016.
  */
trait Servlet31SingleApi {

  def set(handler: Option[Servlet31SingleHandler]) : Unit

  def get() : Option[Servlet31SingleHandler]

}

trait Servlet31SingleHandler {
  def handle(req: HttpServletRequest, response: HttpServletResponse) : Unit
}

