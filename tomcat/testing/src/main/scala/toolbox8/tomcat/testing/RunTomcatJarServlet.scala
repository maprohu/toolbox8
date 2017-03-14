package toolbox8.tomcat.testing

import java.io.File

import org.apache.catalina.startup.Tomcat
import org.apache.catalina.webresources.{DirResourceSet, StandardRoot}
import org.apache.tomcat.util.scan.StandardJarScanFilter

/**
  * Created by pappmar on 14/03/2017.
  */
object RunTomcatJarServlet {

  def main(args: Array[String]): Unit = {

    val tomcat = new Tomcat()
    val baseDir = "../toolbox8/target/tomcat"
    new File(baseDir, "webapps").mkdirs()
    tomcat.setBaseDir(baseDir)
    val warDir = new File(baseDir, "wardir")
    warDir.mkdirs()

    val ctx = tomcat.addWebapp("", warDir.getAbsolutePath)

    ctx
      .getJarScanner
      .setJarScanFilter(
        new StandardJarScanFilter {
          this.setTldSkip("*")
        }
      )

    val resources = new StandardRoot(ctx)
    def addClasses(project: String) = {
      val additionWebInfClasses = new File(s"../toolbox8/tomcat/${project}/target/classes");
      resources.addPreResources(
        new DirResourceSet(
          resources,
          "/WEB-INF/classes",
          additionWebInfClasses.getAbsolutePath,
          "/"
        )
      )
    }
    addClasses("jarservlet")
    addClasses("testapp")

    ctx.setResources(resources)




    tomcat.start()
    tomcat.getServer.await()

  }

}
