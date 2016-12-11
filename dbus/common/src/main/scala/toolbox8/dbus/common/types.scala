package toolbox8.dbus.common

import org.freedesktop.DBus
import org.freedesktop.dbus._

object DBTuple {
  val Max = 3
}
class DBTuple2[T1, T2](
  @Position(0)
  var t1: T1,
  @Position(1)
  var t2: T2
) extends Tuple

class DBTuple3[T1, T2, T3](
  @Position(0)
  var t1: T1,
  @Position(1)
  var t2: T2,
  @Position(2)
  var t3: T3
) extends Tuple

class DBStruct2[T1, T2](
  @Position(0)
  var t1: T1,
  @Position(1)
  var t2: T2
) extends Struct

class DBStruct3[T1, T2, T3](
  @Position(0)
  var t1: T1,
  @Position(1)
  var t2: T2,
  @Position(2)
  var t3: T3
) extends Struct

class DBStruct4[T1, T2, T3, T4](
  @Position(0)
  var t1: T1,
  @Position(1)
  var t2: T2,
  @Position(2)
  var t3: T3,
  @Position(3)
  var t4: T4
) extends Struct



class DBInterfaceCompanion[I <: DBusInterface](cls: Class[I], interfaceName: String) {

  sealed trait BaseProperty[T] {
    def name: String
  }
  trait ReadProperty[T] extends BaseProperty[T] {
    def read(
      obj: I
    )(implicit
      connection: DBusConnection
    ) : T = {
      DBusTools
        .cast(
          obj,
          classOf[DBus.Properties]
        )
        .Get[T](interfaceName, name)
    }
  }

  trait WriteProperty[T] extends BaseProperty[T]

  def read[T](name_ : String) = new ReadProperty[T] {
    override val name: String = name_
  }

  def write[T](name_ : String) = new WriteProperty[T] {
    override val name: String = name_
  }

  def readwrite[T](name_ : String) = new ReadProperty[T] with WriteProperty[T] {
    override val name: String = name_
  }

  class BusInstances(bus: String) {

    sealed trait Instance {

      def path: String

      def getRemoteObject(implicit
        connection: DBusConnection
      ) : I = {
        connection
          .getRemoteObject[I](
            bus,
            path,
            cls
          )
      }

    }

    def instance(path_ : String) : Instance = new Instance {
      override val path = path_

    }

  }

}