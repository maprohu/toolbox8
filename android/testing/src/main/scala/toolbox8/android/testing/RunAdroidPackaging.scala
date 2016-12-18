package toolbox8.android.testing

import java.io.File

import com.android.builder.core.ErrorReporter.EvaluationMode
import com.android.builder.core.{AndroidBuilder, BuildToolsServiceLoader, ErrorReporter}
import com.android.builder.sdk.TargetInfo
import com.android.ide.common.blame.Message
import com.android.ide.common.process.{JavaProcessExecutor, JavaProcessInfo, ProcessOutputHandler}
import com.android.jack.api.JackProvider
import com.android.jack.api.v03.Api03Config
import com.android.jill.api.v01.Api01Config
import com.android.repository.Revision
import com.android.sdklib.BuildToolInfo
import com.android.utils.StdLogger
import com.android.utils.StdLogger.Level

/**
  * Created by maprohu on 18-12-2016.
  */
object RunAdroidPackaging {

  val logger = new StdLogger(Level.VERBOSE)

  def main(args: Array[String]): Unit = {

    val pe = new com.android.ide.common.process.DefaultProcessExecutor(
      logger
    )

    val jpe = new JavaProcessExecutor {
      override def execute(javaProcessInfo: JavaProcessInfo, processOutputHandler: ProcessOutputHandler) = ???
    }
    val er = new ErrorReporter(EvaluationMode.STANDARD) {
      override def receiveMessage(message: Message) = ???
      override def handleIssue(data: String, `type`: Int, severity: Int, msg: String) = ???
    }

    val builder = new AndroidBuilder(
      "andtest",
      "maprohu",
      pe,
      jpe,
      er,
      logger,
      true
    )

  }

}

object RunJill {
  import scala.collection.JavaConversions._
  import RunAdroidPackaging._

  def main(args: Array[String]): Unit = {
    val buildToolInfo =
      BuildToolInfo.fromStandardDirectoryLayout(
        new Revision(25, 0, 2),
        new File("/media/data/android-sdk/build-tools/25.0.2")
      )
    val buildToolServiceLoader = BuildToolsServiceLoader.INSTANCE.forVersion(buildToolInfo)

    val jill =
      buildToolServiceLoader
        .getSingleService(logger, BuildToolsServiceLoader.JILL)
        .get()
        .createConfig(classOf[Api01Config])

    val wd =
      new File("../toolbox8/android/testing/target/wd")
    wd.mkdirs()

    jill
      .setInputJavaBinaryFile(
        new File("../toolbox6/logging/target/product.jar")
      )

    val jackLibFile =
      new File(wd, "jacklib.out")

    jill
      .setOutputJackFile(
        jackLibFile
      )

    jill
      .getTask
      .run()


    val jack =
      buildToolServiceLoader
        .getSingleService(logger, BuildToolsServiceLoader.JACK)
        .get()
        .createConfig(classOf[Api03Config])

    jack
      .setImportedJackLibraryFiles(
        Vector(jackLibFile)
      )

    jack.setOutputJackFile(
      new File(wd, "jackapp.out")
    )

    jack
      .getTask
      .run()




  }
}