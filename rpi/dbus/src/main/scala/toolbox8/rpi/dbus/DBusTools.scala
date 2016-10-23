package toolbox8.rpi.dbus

import org.freedesktop.DBus
import org.freedesktop.DBus.Introspectable
import org.freedesktop.dbus.{DBusConnection, DBusInterface}

import scala.reflect.ClassTag
import toolbox6.macros.Macros._

import scala.xml.XML
/**
  * Created by maprohu on 23-10-2016.
  */
object DBusTools {

  val `org.freedesktop.DBus` = valName
  val `org.bluez` = valName

  def connect = new Connection(
    conn = DBusConnection.getConnection(DBusConnection.SYSTEM)
  ) {
    def close = conn.disconnect()
  }


  class Connection(
    val conn: DBusConnection
  ) {
    def bluez = bus(`org.bluez`)
    def dbus = bus(`org.freedesktop.DBus`).root[DBus]
    def bus(name: String) = new Bus(name)

    class Bus(
      val busname: String
    ) { bself =>
      def root[T <: DBusInterface : ClassTag] = get[T]("/")

      import Introspection._
      def introspectable(path: String) = get[Introspectable](path)

      def introspect(root: xml.Node, path: String, name: Option[String] = None) : Introspection.Node = {
        def child(n: xml.Node) : Node = {
          val childName = (n \ "@name").text
          val basePath = if (path == "/") "" else path
          val childPath = s"$basePath/$childName"
          if (n.child.isEmpty) {
            introspectable(childPath).introspect(Some(childName))
          } else {
            introspect(n, childPath)
          }

        }

        Node(
          name = name.getOrElse((root \ "@name" headOption).map(_.text).getOrElse(path)),
          path = path,
          interfaces = (root \ "interface").map { i =>
            Interface(
              name = (i \ "@name").text,
              methods = Seq(),
              signals = Seq(),
              properties = Seq()
            )
          },
          children = (root \ "node").map(child)
        )
      }

      def get[T <: DBusInterface : ClassTag](path: String) = {
        val cl : Class[T] = implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]
        new RemoteObject[T](
          path,
          conn.getRemoteObject(
            busname,
            path,
            cl
          )

        )
      }

      class RemoteObject[T](
        val objectpath: String,
        val instance: T
      ) {
        def as[T <: DBusInterface : ClassTag] = get[T](objectpath)

        def introspect(name: Option[String] = None)(implicit ev: T => Introspectable): Node = {
          val xml = ev(instance).Introspect()
          println(xml)
          bself
            .introspect(
              XML
                .withSAXParser(Factory.newSAXParser())
                .loadString(ev(instance).Introspect()),
                objectpath,
              name
            )
        }
      }

    }

  }

}

object Introspection {
  val Factory = {
    val factory = javax.xml.parsers.SAXParserFactory.newInstance()

    // disable DTD validation
    factory.setValidating(false)
    factory.setFeature("http://xml.org/sax/features/validation", false)
    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false)
    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
    factory.setFeature("http://xml.org/sax/features/external-general-entities", false)
    factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false)

    factory
  }

  type TypeSpec = String

  case class Node(
    name: String,
    path: String,
    interfaces: Seq[Interface],
    children: Seq[Node]
  )

  case class Interface(
    name: String,
    methods: Seq[Method],
    signals: Seq[Signal],
    properties: Seq[Property]
  )

  case class Method(
    name: String,
    in: Seq[Arg],
    out: Seq[Arg]
  )


  case class Arg(
    name: Option[String],
    typespec: TypeSpec
  )

  case class Signal(
    name: String,
    args: Seq[Arg]
  )

  sealed trait Direction
  case object Read extends Direction
  case object Write extends Direction
  case object ReadWrite extends Direction

  case class Property(
    name: String,
    typeSpec: TypeSpec,
    direction: Direction
  )

}
