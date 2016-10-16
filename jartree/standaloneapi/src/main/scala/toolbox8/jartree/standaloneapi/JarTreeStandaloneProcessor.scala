package toolbox8.jartree.standaloneapi

import java.nio.ByteBuffer

import org.reactivestreams.{Processor, Subscriber}
import toolbox6.jartree.api.{InstanceResolver, JarUpdatable}


trait Service extends JarUpdatable {
//  def subscriber() : Subscriber[IncomingConnection]
}

trait PeerInfo {
  def address() : String
}

trait ByteArray {
  def bytes() : Array[Byte]
  def offset() : Int
  def count() : Int
}

trait IncomingConnection {
  def peerInfo() : PeerInfo
  def flow() : Processor[ByteArray, ByteArray]
}

trait JarTreeStandaloneContext extends InstanceResolver {

}