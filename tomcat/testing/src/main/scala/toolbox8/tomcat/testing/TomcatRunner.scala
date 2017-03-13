package toolbox8.tomcat.testing

import java.io.File

import org.apache.catalina.startup.Tomcat
import org.apache.tomcat.util.scan.StandardJarScanFilter

/**
  * Created by pappmar on 13/03/2017.
  */
object TomcatRunner {

  def run(war: String): Unit = {
    val tomcat = new Tomcat()
    val baseDir = "../toolbox8/target/tomcat"
    new File(baseDir, "webapps").mkdirs()
    tomcat.setBaseDir(baseDir)

    val ctx = tomcat.addWebapp("", war)

    ctx
      .getJarScanner
      .setJarScanFilter(
        new StandardJarScanFilter {
          this.setTldSkip("*")
        }
      )

    tomcat.start()
    tomcat.getServer.await()
  }

}
