package toolbox8.jartree.installer

import maven.modules.builder.MavenTools
import toolbox8.modules.Toolbox8Modules

import scala.io.StdIn
import ammonite.ops._

/**
  * Created by martonpapp on 19/10/16.
  */
object RunDummyInstaller {

  def main(args: Array[String]): Unit = {

    MavenTools
      .runMaven(
        MavenTools.pom(
          <build>
            <finalName>voicer</finalName>
            <plugins>
              <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <configuration>
                  <archive>
                    <manifest>
                      <addClasspath>true</addClasspath>
                      <mainClass>toolbox8.dummy.DummyMain</mainClass>
                    </manifest>
                  </archive>
                  <outputDirectory>target/lib</outputDirectory>
                </configuration>
              </plugin>
              <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <version>2.10</version>
                <executions>
                  <execution>
                    <id>copy-dependencies</id>
                    <phase>package</phase>
                    <goals>
                      <goal>copy-dependencies</goal>
                    </goals>
                    <configuration>
                      <outputDirectory>target/lib</outputDirectory>
                    </configuration>
                  </execution>
                </executions>
              </plugin>
              </plugins>
            </build>
          <dependencies>
            {
            Toolbox8Modules.Dummy.pomDependency
            }
          </dependencies>
        ),
        "package"
      ) { dir =>
        StdIn.readLine()

      }

  }

}
