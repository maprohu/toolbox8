package toolbox8.jartree.akka

import java.net.{URL, URLClassLoader}

import akka.actor.ActorRef
import com.typesafe.scalalogging.LazyLogging
import toolbox8.jartree.common.JarKey

import scala.concurrent.{ExecutionContext, ExecutionException, Future}
import scala.util.Try
import scala.collection.immutable._

/**
  * Created by maprohu on 06-11-2016.
  */
class ParentLastUrlClassloader(
  urls: Iterable[URL],
  parent: ClassLoader
) extends URLClassLoader(urls.toArray, parent) { self =>

  override def loadClass(name: String, resolve: Boolean): Class[_] = {
    Option(findLoadedClass(name))
      .orElse(
        Try(findClass(name)).toOption
      )
      .map({ c =>
        if (resolve) {
          resolveClass(c)
        }
        c
      })
      .getOrElse(
        super.loadClass(name, resolve)
      )
  }
}

object ParentLastUrlClassloader extends LazyLogging {
  def apply(
    jars: Iterable[JarKey],
    parent: ClassLoader,
    jarCacheActor: ActorRef
  )(implicit
    executionContext: ExecutionContext
  ) : Future[ParentLastUrlClassloader] = {
    import akka.pattern._
    import toolbox8.akka.actor.ActorImplicits._

    jarCacheActor
      .ask(
        JarCacheActor.Get(
          keys = jars
        )
      )
      .mapTo[JarCacheActor.GetResponse]
      .map({ path =>
        logger.debug(s"classloader: ${path}")
        new ParentLastUrlClassloader(
          path
            .jars
            .map(_.toFile.toURI.toURL),
          parent
        )
      })
  }
}
