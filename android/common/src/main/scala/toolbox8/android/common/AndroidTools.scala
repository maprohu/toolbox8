package toolbox8.android.common

import android.app.Activity

import scala.concurrent.{Future, Promise}
import scala.util.Try

/**
  * Created by pappmar on 19/12/2016.
  */
object AndroidTools {

  def runOnUiThread[T](
    activity: Activity
  )(
    fn: => T
  ): Future[T] = {
    val promise = Promise[T]()
    activity.runOnUiThread(
      new Runnable {
        override def run(): Unit = {
          promise.complete(Try(fn))
        }
      }
    )
    promise.future
  }

}
