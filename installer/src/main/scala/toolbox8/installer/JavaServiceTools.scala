package toolbox8.installer

import java.io.{ByteArrayInputStream, File}

import mvnmod.builder.{MavenTools, Module, NamedModule}
import toolbox6.ssh.SshTools.Config

/**
  * Created by martonpapp on 20/10/16.
  */
object JavaServiceTools {

  def unit(
    name: String,
    user: String,
    bindAddress: String,
    port: Int,
    debug: Boolean = false
  ) = {
    s"""
       |[Unit]
       |Description=${name}
       |[Service]
       |ExecStart=/usr/bin/java ${if (debug) "-agentlib:jdwp=transport=dt_socket,server=y,address=8000,suspend=n" else ""} -jar /opt/${name}/lib/${name}.jar ${name} ${bindAddress} ${port}
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

  def uploadWithPom(
    name: String,
    module: NamedModule,
    mainClass: String,
    user: String = "pi",
    bindAddress: String,
    port: Int
  )(implicit
    target: Config
  ) = {
    val pom = downloaderPom(
      module,
      s"/opt/${name}/lib"
    )

    import ammonite.ops._
    import toolbox6.ssh.SshTools._
    implicit val session = connect


    val targetRoot = s"/opt/${name}"
    val targetLib = s"${targetRoot}/lib"
    command(s"sudo mkdir -p ${targetLib} && sudo chown -R ${target.user}: ${targetRoot}")

    val pomFile = "/tmp/pom.xml"

    val ba = pom.toString().getBytes

    scpStream(
      () => new ByteArrayInputStream(ba),
      ba.length,
      pomFile
    )

    commandInteractive(
      s"mvn -f ${pomFile} package"
    )

    MavenTools
      .runMaven(
        MavenTools.pom(
          <build>
            <finalName>{name}</finalName>
            <plugins>
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
              module
                .resolve
                .toSeq
                .filter(!_.source.isEmpty)
                .distinct
                .map({ m =>
                  <dependency>
                    {m.version.asPomCoordinates}
                    <exclusions>
                      <exclusion>
                        <groupId>*</groupId>
                        <artifactId>*</artifactId>
                      </exclusion>
                    </exclusions>
                  </dependency>
                })
              }
            </dependencies>
        ),
        "package"
      ) { dir =>
        ls(Path(dir.getAbsoluteFile) / 'target / 'lib)
          .foreach({ jar =>
            scp(
              new File(jar.toString()),
              s"${targetLib}/${jar.name}"
            )
          })


      }

      buildMain(
        name,
        module,
        mainClass
      ) { dir =>
        scp(
          new File(dir, s"target/lib/${name}.jar"),
          s"${targetLib}/${name}.jar"
        )
      }

    commandInteractive(s"sudo chown -R ${user}: ${targetRoot}")
    commandInteractive(s"sudo systemctl status ${name}")
    commandInteractive(s"sudo systemctl stop ${name}")
    commandInteractive(s"sudo systemctl disable ${name}")
//    command(s"sudo rm -rf /opt/${name}/data")
    commandInteractive(JavaServiceTools.installCommand(name, user, bindAddress, port))
    commandInteractive("sudo systemctl daemon-reload")
    commandInteractive(s"sudo systemctl enable ${name}")
    commandInteractive(s"sudo systemctl start ${name}")
    commandInteractive(s"sudo systemctl status ${name}")

    session.disconnect()
  }


  def buildMain(
    name: String,
    module: NamedModule,
    mainClass: String
  )(andThen: File => Unit) = {
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
            </plugins>
          </build>
          <dependencies>
            {
            module.pomDependency
            }
          </dependencies>
        ),
        "package"
      )(andThen)

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
        import toolbox6.ssh.SshTools._
        implicit val session = connect


        val targetRoot = s"/opt/${name}"
        val targetLib = s"${targetRoot}/lib"
        command(s"sudo mkdir -p ${targetLib} && sudo chown -R ${target.user}: ${targetRoot}")
        ls(Path(dir.getAbsoluteFile) / 'target / 'lib)
          .foreach({ jar =>
            scp(
              new File(jar.toString()),
              s"${targetLib}/${jar.name}"
            )
          })
        command(s"sudo chown -R ${user}: ${targetRoot}")
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

  def downloaderPom(
    module: Module,
    targetDir: String
  ) = {
    MavenTools.pom(
      <build>
        <plugins>
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
                  <outputDirectory>{targetDir}</outputDirectory>
                </configuration>
              </execution>
            </executions>
          </plugin>
        </plugins>
      </build>
      <dependencies>
        {
        module
          .resolve
          .toSeq
          .filter(_.source.isEmpty)
          .distinct
          .map(_.version.asPomDependency)
        }
      </dependencies>
      <repositories>
        {
        module.repos.map { r =>
          <repository>
            <id>{r.id}</id>
            <url>{r.url}</url>
          </repository>
        }
        }
      </repositories>
    )

  }


}
