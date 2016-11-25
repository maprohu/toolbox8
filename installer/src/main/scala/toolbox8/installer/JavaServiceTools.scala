package toolbox8.installer

import java.io.File

import mvnmod.builder.{MavenTools, NamedModule}
import toolbox8.installer.SshTools.Config

/**
  * Created by martonpapp on 20/10/16.
  */
object JavaServiceTools {

  def unit(name: String, user: String, bindAddress: String, port: Int) = {
    s"""
       |[Unit]
       |Description=${name}
       |[Service]
       |ExecStart=/usr/bin/java -agentlib:jdwp=transport=dt_socket,server=y,address=8000,suspend=n -jar /opt/${name}/lib/${name}.jar ${name} ${bindAddress} ${port}
       |User=${user}
       |SuccessExitStatus=143
       |[Install]
       |WantedBy=multi-user.target
     """.stripMargin
  }

  def installCommand(
    name: String,
    user: String,
    bindAddress: String,
    port: Int
  ) = {
   s"""sudo tee /etc/systemd/system/${name}.service > /dev/null << EOF
      |${unit(name, user, bindAddress, port)}
      |EOF
    """.stripMargin
  }

  def upload(
    name: String,
    module: NamedModule,
    mainClass: String,
    user: String = "pi",
    bindAddress: String,
    port: Int
  )(implicit
    target: Config
  ) = {
    MavenTools
      .runMaven(
        MavenTools.pom(
          <build>
            <finalName>{name}</finalName>
            <plugins>
              <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <configuration>
                  <archive>
                    <manifest>
                      <addClasspath>true</addClasspath>
                      <mainClass>{mainClass}</mainClass>
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
              module.pomDependency
              }
            </dependencies>
        ),
        "package"
      ) { dir =>
        import ammonite.ops._
        import toolbox8.installer.SshTools._
        implicit val session = connect


        val targetRoot = s"/opt/${name}"
        val targetLib = s"${targetRoot}/lib"
        command(s"sudo mkdir -p ${targetLib} && sudo chown -R ${user}: ${targetRoot}")
        ls(Path(dir.getAbsoluteFile) / 'target / 'lib)
          .foreach({ jar =>
            scp(
              new File(jar.toString()),
              s"${targetLib}/${jar.name}"
            )
          })
        command(s"sudo systemctl status ${name}")
        command(s"sudo systemctl stop ${name}")
        command(s"sudo systemctl disable ${name}")
        command(s"sudo rm -rf /opt/${name}/data")
        command(JavaServiceTools.installCommand(name, user, bindAddress, port))
        command("sudo systemctl daemon-reload")
        command(s"sudo systemctl enable ${name}")
        command(s"sudo systemctl start ${name}")
        command(s"sudo systemctl status ${name}")

        session.disconnect()

      }

  }



}
