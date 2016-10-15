package toolbox8.rpi

import ammonite.ops.Path
import ammonite.ops._
import com.jcraft.jsch._
/**
  * Created by martonpapp on 15/10/16.
  */
object RpiInstaller {

  case class Config(
    host: String,
    port: Int = 22,
    user: String = "pi",
    key: Path = home / ".ssh" / "id_rsa"
  )

  def connect(config: Config) = {
    val jsch = new JSch
    jsch.addIdentity(config.key.toString())
    val session = jsch.getSession(config.user, config.host, config.port)
    session.setUserInfo(AcceptAllUserInfo)
    session.connect()
    session
  }



  def run(
    command: String
  )(implicit
    config: Config
  ) = {
    val session = connect(config)
    val channel = session.openChannel("exec").asInstanceOf[ChannelExec]
    channel.setCommand(command)
    channel.setInputStream(null)
    channel.setErrStream(System.err)
    val in = channel.getInputStream
    channel.connect()

    val buff = Array.ofDim[Byte](1024)
    Iterator
      .continually(in.read(buff))
      .takeWhile(_ != -1)
      .map(buff.take)
      .foreach(System.out.write)

    println(s"\n--------------\nexit-status: ${channel.getExitStatus}")

    channel.disconnect()
    session.disconnect()




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
