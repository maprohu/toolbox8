package toolbox8.rpi.dbus

import org.freedesktop.dbus.{Position, Tuple}

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


