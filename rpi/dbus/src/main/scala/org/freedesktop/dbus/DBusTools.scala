package org.freedesktop.dbus

/**
  * Created by maprohu on 10-12-2016.
  */
object DBusTools {

  def cast[T <: DBusInterface](obj: DBusInterface, cls: Class[T]) : T = {
    val ih =
      java.lang.reflect.Proxy
        .getInvocationHandler(obj)
        .asInstanceOf[RemoteInvocationHandler]

    ih
      .conn
      .asInstanceOf[DBusConnection]
      .getRemoteObject[T](
        ih.remote.busname,
        ih.remote.objectpath,
        cls
      )
  }

}
