package toolbox8.installer

import java.io.{File, FileInputStream, InputStream, OutputStream}

import ammonite.ops.Path
import com.jcraft.jsch._

/**
  * Created by martonpapp on 15/10/16.
  */
object SshTools {

  trait Config {
    def user: String
    def host: String
    def sshPort: Int
    def key : Path
  }

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
    println(s"running: ${cmd}")
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

  def tunnel(
    forwardPort: Int,
    reversePort: Int
  )(implicit
    target: Config
  ) = {
    implicit val session = connect

    println(s"localhost:${forwardPort} -> ${target.host}")
    session.setPortForwardingL(
      forwardPort,
      "localhost",
      forwardPort
    )

    println(s"localhost:${reversePort} <- ${target.host}")
    session.setPortForwardingR(
      reversePort,
      "localhost",
      reversePort
    )

  }

  case class ForwardTunnel(
    localPort: Int,
    remoteHost: String = "localhost",
    remotePort: Int
  )
  object ForwardTunnel {
    implicit def apply(
      port: Int
    ): ForwardTunnel = ForwardTunnel(
      localPort = port,
      remotePort = port
    )
  }
  case class ReverseTunnel(
    remotePort: Int,
    localHost: String = "localhost",
    localPort: Int
  )
  object ReverseTunnel {
    implicit def apply(
      port: Int
    ): ReverseTunnel = new ReverseTunnel(
      remotePort = port,
      localPort = port
    )
  }

  def tunnels(
    forward: Seq[ForwardTunnel] = Seq.empty,
    reverse: Seq[ReverseTunnel] = Seq.empty
  )(implicit
    target: Config
  ) = {
    implicit val session = connect

    forward
      .foreach({ f =>
        import f._
        println(s"localhost:${localPort} -> (${target.host}:${target.sshPort}) -> ${remoteHost}:${remotePort}")
        session.setPortForwardingL(
          localPort,
          remoteHost,
          remotePort
        )
      })

    reverse.foreach({ r =>
      import r._
      println(s"${localHost}:${localPort} <- (${target.host}:${target.sshPort}) <- localhost:${remotePort}")
      session.setPortForwardingR(
        remotePort,
        localHost,
        localPort
      )
    })

    session
  }

}
