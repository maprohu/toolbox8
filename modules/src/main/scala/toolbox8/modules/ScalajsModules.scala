package toolbox8.modules

import mvnmod.builder.{ScalaModule, SubModuleContainer}

/**
  * Created by martonpapp on 31/08/16.
  */
object ScalajsModules {

  implicit val Container = SubModuleContainer(Toolbox8Modules.Root, "scalajs")

  object JsDocGen extends ScalaModule(
    "jsdocgen",
    mvn.`com.fasterxml.jackson.module:jackson-module-scala_2.11:jar:2.8.6`,
    mvn.`com.lihaoyi:upickle_2.11:jar:0.4.2`,
    mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.8.2`
  )

  object Generator extends ScalaModule(
    "generator",
    JsDocGen
  )

  object O3D extends ScalaModule(
    "o3d"
  )

}
