package toolbox8.scalajs.jsdocgen.generator

import java.io.File
import java.net.URI



/**
 * Created by pappmar on 12/11/2015.
 */
object JsdocInvoker {

  def run(
    inputDir: File,
    jsdocInputs : Seq[String] = Seq("."),
    out: File,
    jsdoc: Seq[String] = Seq("cmd", "/C", "jsdoc"),
    jsdocOptions : Seq[String] = Seq("--explain", "--recurse")
  ): Unit = {
    import ammonite.ops._
    println("running jsdoc...")

    out.getParentFile.mkdirs()

    println(s"jsdoc source base directory: ${inputDir}")
    import ImplicitWd._
    val json = %%(
      jsdoc ++
      jsdocOptions ++
      jsdocInputs.map { input =>
        val inputFile = new File(input)

        val f =
          if (inputFile.isAbsolute) input else
          new File(inputDir, input).getCanonicalPath

        println(s"jsdoc source: ${f}")

        f
      }
    ).out.trim

    write.over(
      Path(out.getCanonicalFile.getAbsoluteFile),
      json
    )

    println("jsdoc complete.")
  }

}
