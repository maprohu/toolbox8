package toolbox8.tomcat.packaging

import java.io.File

import mvnmod.builder._
import toolbox8.modules.Tomcat8Modules

/**
  * Created by pappmar on 13/03/2017.
  */
object TomcatPackager {

  case class Input(
    coords: HasMavenCoordinates = MavenCoordinatesImpl("jarservlet", "jarservlet", "2-SNAPSHOT"),
    jars: Seq[HasMavenCoordinates] = Seq.empty,
    runtime: Module = Tomcat8Modules.JarServlet,
    repos: Seq[Repo] = Seq.empty,
    container: Module = mvn.`javax.servlet:javax.servlet-api:jar:3.1.0`
  )

  def process(
    input: Input = Input()
  )(andThenWar : File => Unit ) = {
    import input._

    val pom =
      MavenTools
        .pom(
          coords,
          <packaging>war</packaging>
          <build>
            <finalName>product</finalName>
            <plugins>
              <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.0.0</version>
              </plugin>
            </plugins>
          </build>
            <dependencyManagement>
              <dependencies>
                {
                container.deps.map({ (m:Module) =>
                  <dependency>
                    {m.version.asPomCoordinates}
                    <scope>provided</scope>
                  </dependency>
                })
                }
              </dependencies>
            </dependencyManagement>
            <dependencies>
              {
              input.runtime.version.asPomDependency
              }
            </dependencies>
            <repositories>
              {
              repos.map { r =>
                <repository>
                  <id>{r.id}</id>
                  <url>{r.url}</url>
                </repository>
              }
              }
            </repositories>
        )

    MavenTools
      .runMaven(
        pom,
        "install"
      ) { file =>
        andThenWar(new File(file, "target/product.war"))
      }
  }


}
