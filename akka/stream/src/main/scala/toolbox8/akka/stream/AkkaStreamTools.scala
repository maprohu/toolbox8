package toolbox8.akka.stream

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Materializer, Supervision}
import com.typesafe.scalalogging.LazyLogging

/**
  * Created by pappmar on 19/10/2016.
  */
object AkkaStreamTools extends LazyLogging {

  trait Components {
    implicit val actorSystem: ActorSystem
    implicit val materializer: Materializer
  }

  def bootstrap() : Components = {
    implicit val _actorSystem = ActorSystem()

    implicit val _materializer =
      ActorMaterializer(
        Some(
          ActorMaterializerSettings(_actorSystem)
            .withSupervisionStrategy({ ex:Throwable =>
              logger.error(ex.getMessage, ex)

              Supervision.Stop
            })
        )
      )

    new Components {
      override implicit val actorSystem: ActorSystem = _actorSystem
      override implicit val materializer: Materializer = _materializer
    }
  }

}