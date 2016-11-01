package toolbox8.jartree.standaloneapi

import java.net.InetSocketAddress
import java.nio.ByteBuffer

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import toolbox6.jartree.api.{InstanceResolver, JarCacheLike}

import scala.concurrent.Future

trait Service {
  def apply(info: PeerInfo) : Future[Flow[ByteString, ByteString, _]]
  def close() : Unit
}


case class PeerInfo(
  address: InetSocketAddress,
  id: Option[String] = None
)

trait JarTreeStandaloneContext extends InstanceResolver {

  def jarCache: JarCacheLike
  implicit val actorSystem : ActorSystem
  implicit val materializer: Materializer

}