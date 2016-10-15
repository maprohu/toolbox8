package toolbox8.jartree.standaloneapi

import org.reactivestreams.{Processor, Subscriber}


trait Service {
  def subscriber() : Subscriber[IncomingConnection]
}

trait PeerInfo {
  def address() : String
}

trait IncomingConnection {
  def peerInfo() : PeerInfo
  def flow() : Processor[Array[Byte], Array[Byte]]
}

trait JarTreeStandaloneContext {

}