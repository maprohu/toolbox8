package toolbox8.jartree.standaloneapi

import java.net.InetSocketAddress
import java.nio.ByteBuffer

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import toolbox6.jartree.api.InstanceResolver

import scala.concurrent.Future

//object Message {
//  type Header = Byte
//}

//trait Message {
//  def header() : Message.Header
//  def data() : java.util.Enumeration[ByteBuffer]
//}

//case class Message(
//  header: Message.Header,
//  data: ByteString
//)



trait Service {
  def apply(info: PeerInfo) : Future[Flow[ByteString, ByteString, _]]
  def close() : Unit
}


case class PeerInfo(
  address: InetSocketAddress,
  id: Option[String] = None
)

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

  implicit def actorSystem : ActorSystem
  implicit def materializer: Materializer

}