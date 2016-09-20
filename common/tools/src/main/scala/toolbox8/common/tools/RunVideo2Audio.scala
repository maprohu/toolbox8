package toolbox8.common.tools

import ammonite.ops._
import ammonite.ops.ImplicitWd._

import scala.util.Try
/**
  * Created by martonpapp on 20/09/16.
  */
object RunVideo2Audio {

  def main(args: Array[String]): Unit = {

    val dir = home/'Downloads/'coursera

    ls! dir |? (_.name.endsWith(".mp4")) foreach { f =>
      Try {
        %(home/'bin/'ffmpeg, "-n", "-i", f.toString(),
          "-af", "volume=2.0", f.toString().init :+ '3')
      }
    }



  }

}
