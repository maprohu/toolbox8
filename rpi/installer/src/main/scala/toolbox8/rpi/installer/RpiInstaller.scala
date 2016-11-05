package toolbox8.rpi.installer

import java.io.{File, FileInputStream, InputStream, OutputStream}

import ammonite.ops.{Path, _}
import com.jcraft.jsch._
import toolbox8.jartree.standaloneapi.Protocol

/**
  * Created by martonpapp on 15/10/16.
  */
object RpiInstaller {

  case class Config(
    host: String,
    servicePort : Int = Protocol.DefaultPort,
    akkaPort : Int = Protocol.AkkaDefaultPort,
    sshPort: Int = 22,
    user: String = "pi",
    key: Path = home / ".ssh" / "id_rsa"
  )

  def connect(implicit
    config: Config
  ) = {
    val jsch = new JSch
    jsch.addIdentity(config.key.toString())
    val session = jsch.getSession(config.user, config.host, config.sshPort)
    session.setUserInfo(AcceptAllUserInfo)
    session.connect()
    session
  }



  def run(
    cmd: String
  )(implicit
    config: Config
  ) = {
    implicit val session = connect(config)
    val es = command(cmd)
    println(s"\n--------------\nexit-status: ${es}")
  }

  def command(
    cmd: String
  )(implicit
    session: Session
  ) = {
    val channel = session.openChannel("exec").asInstanceOf[ChannelExec]
    try {
      channel.setCommand(cmd)
      channel.setInputStream(null)
      channel.setErrStream(System.err)
      val in = channel.getInputStream
      channel.connect()

      copy(in, System.out)

      channel.getExitStatus
    } finally {
      channel.disconnect()
    }
  }

  def exec(
    cmd: String,
    proc: (Channel, InputStream, OutputStream) => Unit
  )(implicit
    session: Session
  ) = {
    val channel = session.openChannel("exec").asInstanceOf[ChannelExec]
    try {
      println(cmd)
      channel.setCommand(cmd)
      val out = channel.getOutputStream
      val in = channel.getInputStream
      channel.connect()

      proc(channel, in, out)

      channel.getExitStatus
    } finally {
      channel.disconnect()
    }

  }

  def copy(
    from: InputStream,
    to: OutputStream
  ) = {
    val buff = Array.ofDim[Byte](1024)
    Iterator
      .continually(from.read(buff))
      .takeWhile(_ != -1)
      .map(buff.take)
      .foreach(to.write)
    to.flush()
  }



  def scp(
    from: File,
    to: String
  )(implicit
    session: Session
  ) = {
    exec(
      s"scp -t ${to}",
      { (ch, in, out) =>
        def check = {
          println(in.read())
        }
        check

        val cmd = s"C0644 ${from.length()} ${to.reverse.takeWhile(_ != '/').reverse}\n"
        print(cmd)
        out.write(cmd.getBytes)
        out.flush()
        check
        val fis = new FileInputStream(from)
        copy(fis, out)
        fis.close()
        out.write(0)
        out.flush()
        check
        out.close()

        in.close()
      }
    )


  }

  val AcceptAllUserInfo = new UserInfo {
    override def promptPassword(message: String): Boolean = ???

    override def promptYesNo(message: String): Boolean = true

    override def showMessage(message: String): Unit = ???

    override def getPassword: String = ???

    override def promptPassphrase(message: String): Boolean = ???

    override def getPassphrase: String = ???
  }


}
