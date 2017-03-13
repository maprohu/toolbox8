package toolbox8.tomcat.packaging

import java.io.File
import ammonite.ops._


/**
  * Created by pappmar on 13/03/2017.
  */
object RunTomcatPackager {

  val WarFile = pwd / up / "toolbox8" / "target" / "tomcat" / "wars" / "jarservlet.war"

  def main(args: Array[String]): Unit = {
    TomcatPackager.process() { war =>
      mkdir(WarFile / up)
      cp.over(Path(war.getAbsolutePath), WarFile)
    }
  }

}
