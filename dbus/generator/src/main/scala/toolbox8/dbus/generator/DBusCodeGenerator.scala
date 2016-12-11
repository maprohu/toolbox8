package toolbox8.dbus.generator

import java.io.File
import java.lang.reflect.Type
import java.util

import org.freedesktop.dbus.{DBusInterface, DBusInterfaceName, Marshalling, Struct}
import sbt.io.IO
import toolbox8.dbus.common._

import scala.reflect.ManifestFactory

/**
  * Created by pappmar on 09/12/2016.
  */
object DBusCodeGenerator {


  def run(
    dir: File,
    structure: DBReflection
  ) : Unit = {
    dir.mkdirs()

    structure
      .interfaces
      .values
      .foreach({ iface =>

        val packageSegments :+ ifaceName = iface.name.segments

        val packageSegmentsLow = packageSegments.map(_.toLowerCase)

        val packageDir = packageSegmentsLow.foldLeft(dir)({new File(_, _)})
        packageDir.mkdirs()

        val scalaFile = new File(packageDir, s"${ifaceName}.scala")

        val methods =
          iface
            .methods
            .map({ m =>

              def singleType(single: DBArg) = {
                Ref.getJavaType(single.typeString)
              }
              def singleArg(single: DBArg) = {
                s"${single.name}: ${singleType(single)}"
              }

              val returnTypeString =
                m.out match {
                  case Seq() => "scala.Unit"
                  case Seq(single) => singleType(single)
                  case other if other.size <= DBTuple.Max =>
                    val count = other.size

                    s"${DBTuple.getClass.getName.init}${count}[${other.map(singleType).mkString(", ")}]"
                  case _ => ???
                }

              val argsString =
                m.in match {
                  case Seq() => ""
                  case Seq(single) => singleArg(single)
                  case multi =>
                    s"""
                      |    ${multi.map(singleArg).mkString(",\n    ")}
                      |  """.stripMargin
                }

              s"""  def ${m.name}(${argsString}) : ${returnTypeString}
               """.stripMargin
            })

        val props =
          iface
            .properties
            .map({ p =>
              val propertyAccess =
                p.access match {
                  case DBRead => "read"
                  case DBWrite => "write"
                  case DBReadWrite => "readwrite"
                }

              s"""    val ${p.name} = ${propertyAccess}[${Ref.getJavaType(p.typeString)}]("${p.name}")"""
            })

        val busInstances =
          iface
            .instances
            .groupBy(_.bus)
            .mapValues(_.map(_.path))
            .toSeq
            .sortBy(_._1)
            .map({
              case (bus, paths) =>
                val is =
                  paths
                    .map({ p =>
                      s"""      val `${p}` = instance("${p}")"""
                    })
                s"""    object `${bus}` extends BusInstances("${bus}") {
                   |${is.mkString("\n")}
                   |    }
                 """.stripMargin
            })

        val dbusInterfaceName = iface.name.segments.mkString(".")

        IO.write(
          scalaFile,
          s"""package ${packageSegmentsLow.mkString(".")}
             |
             |@${classOf[DBusInterfaceName].getName}("${dbusInterfaceName}")
             |trait ${ifaceName} extends ${classOf[DBusInterface].getName} {
             |
             |${methods.mkString("\n")}
             |}
             |
             |object ${ifaceName} extends ${classOf[DBInterfaceCompanion[_]].getName}[${ifaceName}](classOf[${ifaceName}], "${dbusInterfaceName}") {
             |
             |  object Props {
             |${props.mkString("\n")}
             |  }
             |
             |  object Instances {
             |
             |${busInstances.mkString("\n")}
             |  }
             |
             |}
           """.stripMargin
        )

      })

  }

}

object Ref {

  def getJavaType(typeString: String): String = {
    val result = new util.ArrayList[Type]()
    Marshalling.getJavaType(
      typeString,
      result,
      -1
    )
    val jt = result.get(0)
    Ref.javaType(jt).toString()
  }

  import java.lang.reflect.{ Type => JType, Array => _, _ }
  import scala.reflect.Manifest.{ classType, intersectionType, arrayType, wildcardType }

  def intersect(tps: JType*): Manifest[_] = intersectionType(tps map javaType: _*)
  def javaType(tp: JType): Manifest[_] = tp match {
    case x: Class[_]            =>
      if (x.getTypeParameters.isEmpty) {
        classType(x)
      } else {
        val ts =
          x.getTypeParameters.map(_ => wildcardType(ManifestFactory.Nothing, ManifestFactory.Nothing))



        classType(
          x,
          ts(0),
          ts.tail:_*
        )
      }

    case x: ParameterizedType   =>
      val owner = x.getOwnerType
      val raw   = x.getRawType() match {
        case clazz: Class[_] if clazz == classOf[Struct] =>
          x.getActualTypeArguments.length match {
            case 2 => classOf[DBStruct2[_, _]]
            case 3 => classOf[DBStruct3[_, _, _]]
            case 4 => classOf[DBStruct4[_, _, _, _]]
            case _ => ???
          }
        case clazz: Class[_] => clazz
      }
      val targs = x.getActualTypeArguments() map javaType

      (owner == null, targs.isEmpty) match {
        case (true, true)   => javaType(raw)
        case (true, false)  => classType(raw, targs.head, targs.tail: _*)
        case (false, _)     => classType(javaType(owner), raw, targs: _*)
      }
    case x: GenericArrayType    => arrayType(javaType(x.getGenericComponentType))
    case x: WildcardType        => wildcardType(intersect(x.getLowerBounds: _*), intersect(x.getUpperBounds: _*))
    case x: TypeVariable[_]     => intersect(x.getBounds(): _*)
  }
}