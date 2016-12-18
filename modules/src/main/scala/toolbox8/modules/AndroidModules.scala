package toolbox8.modules

import mvnmod.builder.{ScalaModule, SubModuleContainer}
import mvnmod.modules.MvnmodModules

/**
  * Created by martonpapp on 31/08/16.
  */
object AndroidModules {

  implicit val Container = SubModuleContainer(Toolbox8Modules.Root, "android")

  object Packaging extends ScalaModule(
    "packaging",
    MvnmodModules.Common,
    JarTree8Modules.Client,
    mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.8.0`,
    mvn.`com.android.tools:sdk-common:jar:25.2.0`,
    mvn.`com.android.tools:common:jar:25.2.0`,
    mvn.`com.android.tools.build:builder-model:jar:2.2.0`,
    mvn.`com.android.tools.jack:jack-api:jar:0.11.0`,
    mvn.`com.android.tools.jill:jill-api:jar:0.10.0`,
    mvn.`com.android.tools:sdklib:jar:25.2.0`,
    mvn.`com.android.tools:repository:jar:25.2.0`,
    mvn.`com.google.guava:guava:jar:18.0`,
    mvn.`com.android.tools.build:builder:jar:2.2.0`
  )

  object Testing extends ScalaModule(
    "testing",
    Packaging,
    Toolbox8Modules.Modules
  )




}
