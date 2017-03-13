package toolbox8.tomcat.jarservlet

import java.nio.ByteBuffer
import javax.websocket.{OnMessage, Session}
import javax.websocket.server.ServerEndpoint

@ServerEndpoint("/private/ws")
class JarServletWsEndpoint {

  @OnMessage
  def binary(
    session: Session,
    bb: ByteBuffer,
    last: Boolean
  ) : Unit = {

  }



}
