package toolbox8.jartree.requests

import java.io.{InputStream, ObjectInputStream, OutputStream}

import toolbox8.jartree.streamapp.{ClassLoaderConfig, Requestable, RootContext}

/**
  * Created by pappmar on 06/12/2016.
  */
class PluggedRequestable extends Requestable {

  override def request(ctx: RootContext, in: InputStream, out: OutputStream): Unit = {
    val dis = new ObjectInputStream(in)

    val clc =
      dis
        .readObject()
        .asInstanceOf[ClassLoaderConfig[Requestable]]

    val req =
      ctx.cache.loadInstance(
        clc,
        ctx.holder.get.plugged.getClass.getClassLoader
      )

    req.request(ctx, in, out)
  }

}
