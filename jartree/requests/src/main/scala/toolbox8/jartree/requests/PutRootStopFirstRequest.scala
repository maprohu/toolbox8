package toolbox8.jartree.requests

import java.io.{InputStream, ObjectInputStream, OutputStream}

import com.typesafe.scalalogging.StrictLogging
import toolbox6.logging.LogTools
import toolbox8.jartree.streamapp._

/**
  * Created by pappmar on 06/12/2016.
  */
class PutRootStopFirstRequest extends Requestable with StrictLogging with LogTools {
  override def request(ctx: RootContext, in: InputStream, out: OutputStream): Unit = {
    logger.info("putting root")
    val dos = new ObjectInputStream(in)



    logger.info("getting old plugged")
    val oldPlugged =
      ctx
        .holder
        .get

    logger.info("stopping old plugged")
    quietly { oldPlugged.plugged.stop() }

    logger.info("reading class loader config")
    val clc =
      dos
        .readObject()
        .asInstanceOf[ClassLoaderConfig[Root]]

    logger.info(
      s"""plugging '${clc.className}' from:
        |${clc.jars.mkString("\n")}
      """.stripMargin
    )

    logger.info("creating new root instance")
    val newRoot =
      ctx
        .cache
        .loadInstance(
          clc,
          ctx.parent
        )

    logger.info("creating new plugged instance")
    val newPlugged =
      newRoot
        .plug(
          PlugParams(
            ctx.cache,
            ctx.dataDir
          )
        )

    logger.info("replacing plugged")
    ctx
      .holder
      .set(
        PluggedConfig(
          newPlugged,
          clc
        )
      )


    logger.info("persisting new root config")
    ctx.persist()

    logger.info("put root complete")
  }
}
