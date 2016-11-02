package toolbox8.akka.actor

import akka.util.Timeout
import scala.concurrent.duration._

/**
  * Created by maprohu on 02-11-2016.
  */
trait ActorImplicits {

  implicit val timeout = Timeout(10.seconds)

}

object ActorImplicits extends ActorImplicits
