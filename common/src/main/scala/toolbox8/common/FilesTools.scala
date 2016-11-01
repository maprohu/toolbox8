package toolbox8.common

import java.nio.file.{Path, StandardWatchEventKinds}
import java.util.concurrent.TimeUnit

import com.typesafe.scalalogging.LazyLogging
import monix.execution.{Cancelable, CancelableFuture}

import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}

/**
  * Created by maprohu on 31-10-2016.
  */
object FilesTools extends LazyLogging {

  def waitForFile(
    path: Path,
    pollInterval: Duration = 5.seconds
  ) : CancelableFuture[Boolean] = {
    val file = path.toFile

    val parent = path.getParent
    val watcher = parent.getFileSystem().newWatchService()

    val poller =
      if (pollInterval.isFinite()) {
        { () =>
          watcher.poll(
            pollInterval.length,
            pollInterval.unit
          )
        }
      } else {
        { () =>
          watcher.take()
        }
      }

    parent.register(
      watcher,
      StandardWatchEventKinds.ENTRY_CREATE
    )
    val promise = Promise[Boolean]()

    new Thread() {
      override def run(): Unit = {
        try {
          while (!file.exists()) {
            poller()
          }

          promise.trySuccess(true)
          watcher.close()
        } catch {
          case ex : Throwable =>
            logger.info(ex.getMessage)
            promise.tryFailure(ex)
        }
      }
    }.start()

    CancelableFuture(
      promise.future,
      Cancelable({ () =>
        promise.trySuccess(false)
        watcher.close()
      })
    )
  }

}
