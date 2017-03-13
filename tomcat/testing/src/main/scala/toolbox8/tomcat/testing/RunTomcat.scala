package toolbox8.tomcat.testing

import java.io.File

/**
  * Created by pappmar on 13/03/2017.
  */
object RunTomcat {

  def main(args: Array[String]): Unit = {
    TomcatRunner
      .run(
        new File("../toolbox8/target/tomcat/wars/jarservlet.war").getAbsolutePath
      )

  }

}
