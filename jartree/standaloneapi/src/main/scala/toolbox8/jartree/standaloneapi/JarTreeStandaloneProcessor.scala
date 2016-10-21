package toolbox8.jartree.standaloneapi

import java.net.InetSocketAddress
import java.nio.ByteBuffer

import org.reactivestreams.{Processor, Subscriber}
import toolbox6.jartree.api.{InstanceResolver, JarUpdatable}
import toolbox6.javaapi.AsyncFunction

object Message {
  type Header = Byte
}

trait Message {
  def header() : Message.Header
  def data() : java.util.Enumeration[ByteBuffer]
}



trait Service
//  extends JarUpdatable
  extends AsyncFunction[PeerInfo, Processor[Message, Message]] {
  def close() : Unit
}


trait PeerInfo {
  def address() : InetSocketAddress
}

//trait ByteArray {
//  def bytes() : Array[Byte]
//  def offset() : Int
//  def count() : Int
//}

//trait IncomingConnection {
//  def peerInfo() : PeerInfo
//  def flow() : Processor[ByteArray, ByteArray]
//}

trait JarTreeStandaloneContext extends InstanceResolver {

}