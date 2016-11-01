package toolbox8.jartree.extra.client

import akka.stream.scaladsl.Flow
import akka.util.ByteString
import maven.modules.builder.{Module, ModulePath}

/**
  * Created by maprohu on 01-11-2016.
  */
object ExecClient {

  def flow[Ctx](
    module: Module,
    className: String,
    target: ModulePath

  ) : Flow[ByteString, ByteString, _] = {



  }

}
