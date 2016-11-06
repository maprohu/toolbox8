package toolbox8.jartree.akka

//import java.nio.file.Path
//
//import akka.actor.Actor
//
//import scala.collection.immutable._
//
///**
//  * Created by maprohu on 05-11-2016.
//  */
//import toolbox8.jartree.akka.JarTreeActor._
//class JarTreeActor(
//  config: Config
//) extends Actor {
//
//  var runtime : JarTreeBootstrap.Runtime[Processor, Context] = null
//
//
//  @scala.throws[Exception](classOf[Exception])
//  override def preStart(): Unit = {
//    super.preStart()
//    import config._
//    runtime = JarTreeBootstrap
//      .init[Processor, Context](
//        JarTreeBootstrap.Config(
//          contextProvider = { (jt, ctx) =>
//            ()
//          },
//          voidProcessor = (),
//          name = name,
//          dataPath = dataPath.toFile.getAbsolutePath,
//          version = version,
//          initializer = { () =>
//            Initializer(
//              Seq(),
//              None
//            )
//          },
//          closer = { _ => },
//          logFile = logFile.map(_.toFile),
//          storageDir = storageDir.map(_.toFile)
//        )
//      )
//
//  }
//
//
//  @scala.throws[Exception](classOf[Exception])
//  override def postStop(): Unit = {
//    runtime.stop.cancel()
//
//    super.postStop()
//  }
//
//  override def receive: Receive = {
//    case Query =>
//      sender ! runtime.processorSocket.query()
//  }
//}
//
//object JarTreeActor {
//  case class Config(
//    name: String,
//    dataPath: Path,
//    version: Option[String],
//    logFile: Option[Path],
//    storageDir: Option[Path]
//  )
//
//  case object Query
//
//
//}
