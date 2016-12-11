package toolbox8.dbus.generator

import java.io.{InputStream, ObjectOutputStream, OutputStream}

import org.freedesktop.DBus
import org.freedesktop.dbus.{DBusConnection, Position, Tuple}

import scala.xml.{PrettyPrinter, XML}

/**
  * Created by maprohu on 08-12-2016.
  */
object DbusCompiler {

  val services = Seq(
    "org.bluez",
    "org.freedesktop.NetworkManager"
  )

  // socat TCP-LISTEN:7272,reuseaddr,fork UNIX-CONNECT:/var/run/dbus/system_bus_socket
  // DBUS_SYSTEM_BUS_ADDRESS=tcp:host=localhost,port=7272


  def run(
    connFact: () => DBusConnection
  ): DBReflection = {
    val conn = connFact()

    val pp = new PrettyPrinter(1000, 2)

    try {
      val dbus =
        conn
          .getRemoteObject(
            "org.freedesktop.DBus",
            "/",
            classOf[DBus]
          )

      val listNames =
        dbus
          .ListNames()
          .mkString("\n")

      val orgBluezI =
        conn
          .getRemoteObject(
            "org.bluez",
            "/",
            classOf[DBus.Introspectable]
          )


      val is =
        pp.format(
          XML
            .withSAXParser(DbusCompiler.Factory.newSAXParser())
            .loadString(
              orgBluezI.Introspect()
            )
        )

      val ref = DBReflector.run(services, conn)

      ref
        .copy(
          debug = ref.debug ++ Seq(
            listNames,
            is
          )
        )
    } finally {
      conn.disconnect()
    }
  }


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

}

case class DBPackage(
  name: String,
  parent: Option[DBPackage]
) {
  lazy val segments : Seq[String] = {
    parent.toSeq.flatMap(_.segments) :+ name
  }
}

object DBPackage {
  def fromReverseSegments(segments: Seq[String]) : Option[DBPackage] = {
    segments match {
      case head +: tail =>
        Some(
          DBPackage(
            head,
            fromReverseSegments(tail)
          )
        )
      case _ => None
    }

  }
}

case class DBInterfaceName(
  pkg: Option[DBPackage],
  name: String
) {
  lazy val segments : Seq[String] =
    pkg.toSeq.flatMap(_.segments) :+ name

}

object DBInterfaceName {
  def fromName(name: String) : DBInterfaceName = {
    val segmentsReverse =
      name
        .split('.')
        .toSeq
        .reverse

    DBInterfaceName(
      pkg = DBPackage.fromReverseSegments(
        segmentsReverse
          .tail
      ),
      name = segmentsReverse.head
    )

  }
}


case class DBArg(
  name: String,
  typeString: String
)

case class DBMethod(
  name: String,
  in: Seq[DBArg],
  out: Seq[DBArg]
)

sealed trait DBAccess
case object DBRead extends DBAccess
case object DBWrite extends DBAccess
case object DBReadWrite extends DBAccess
case class DBProperty(
  name: String,
  access: DBAccess,
  typeString: String
)

case class DBInstance(
  bus: String,
  path: String
)

case class DBInterface(
  name: DBInterfaceName,
  methods: Seq[DBMethod],
  properties: Seq[DBProperty],
  instances: Seq[DBInstance]
)

case class DBReflection(
  debug: Seq[String] = Seq.empty,
  interfaces: Map[DBInterfaceName, DBInterface] = Map.empty
)