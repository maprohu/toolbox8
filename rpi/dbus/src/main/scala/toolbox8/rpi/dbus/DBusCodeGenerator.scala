package toolbox8.rpi.dbus

import java.io.File
import treehugger.forest._, definitions._, treehuggerDSL._
import treehugger.forest._
import definitions._
import treehuggerDSL._


/**
  * Created by maprohu on 27-10-2016.
  */
object DBusCodeGenerator {

  def generate(
    where: File
  ) = {
    where.mkdirs()

    RootClass

  }

}
