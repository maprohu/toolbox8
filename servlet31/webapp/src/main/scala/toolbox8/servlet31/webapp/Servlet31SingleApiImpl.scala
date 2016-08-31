package toolbox8.servlet31.webapp

import toolbox6.common.SingleRegistry
import toolbox8.servlet31.singleapi.{Servlet31SingleApi, Servlet31SingleHandler}


/**
  * Created by pappmar on 30/08/2016.
  */
class Servlet31SingleApiImpl extends SingleRegistry[Servlet31SingleHandler] with Servlet31SingleApi

