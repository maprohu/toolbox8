package toolbox8.android.packaging

import ammonite.ops.Path
import mvnmod.builder.{Module, NamedModule}

import scala.util.Properties

/**
  * Created by pappmar on 19/12/2016.
  */
object Aptify {
  import Androidify._
  import ammonite.ops._

  def run(
    module: NamedModule,
    dir : Path = pwd / up / "toolbox8" / "android" / "packaging" / "target" / "apt"
  ) = {
    rm(dir)

    val outDir = dir / "out"

    mkdir(dir)
    Jackify
      .jack(
        module,
        outDir.toIO
      )

    val ClassesDexName = "classes.dex"
    val classesDex = outDir / ClassesDexName

    val tools = Path(BuildToolsDir)

    val mf = dir / "AndroidManifest.xml"

    val amfXml =
      <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                package={module.pkg}>
        <uses-permission android:name="android.permission.WAKE_LOCK" />
        <application android:label={module.pkg}>
          <activity android:name=".MainActivity">
            <intent-filter>
              <action android:name="android.intent.action.MAIN"/>
              <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
          </activity>
        </application>
      </manifest>

    write.over(
      mf,
      amfXml.toString()
    )

    val AptUnalignedFileName = "app-unaligned.apk"
    val aptUnalignedFile = outDir / AptUnalignedFileName
    rm(aptUnalignedFile)

    %(
      tools / "aapt",
      "package",
      "-f",
      "-M", mf.toString(),
      "-I", AndroidJar.toString,
      "-F", aptUnalignedFile.toString()
    )(pwd)


    %(
      tools / "aapt",
      "add",
      AptUnalignedFileName,
      ClassesDexName
    )(outDir)

    %(
      "jarsigner",
      "-keystore", s"${Properties.userHome}/.android/debug.keystore",
      "-storepass", "android",
      "-keypass", "android",
      aptUnalignedFile.toString(),
      "androiddebugkey"
    )(pwd)

    val AptFileName = "app.apk"
    val aptAlignedFile = outDir / AptFileName

    %(
      tools / "zipalign",
      "-f", "4",
      aptUnalignedFile.toString(),
      aptAlignedFile.toString()
    )(pwd)










  }

}
