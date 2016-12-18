package toolbox8.android.testing

import toolbox6.modules.Toolbox6Modules
import toolbox8.android.packaging.Jillify

/**
  * Created by maprohu on 18-12-2016.
  */
object RunJillify {

  def main(args: Array[String]): Unit = {

    Jillify.single(
      Toolbox6Modules.Logging
    )
  }

}
