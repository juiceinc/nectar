import sbt._
import java.io.File
import fi.jawsy.sbtplugins.jrebel.JRebelWebPlugin

class NectarProject(info: ProjectInfo) extends AppengineProject(info) with JRebel with JRebelWebPlugin {
  // Add unchecked to the Scala compiler options.
  override def compileOptions = super.compileOptions ++ Seq(Unchecked)

  // Allow connections from the source debugger to the App Engine dev instance.
  override def devAppserverJvmOptions:Seq[String] = List("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005") ++ super.devAppserverJvmOptions

  // Add Maven Local repository for SBT to search for (disable if this doesn't suit you)
  val mavenLocal = "Local Maven Repository" at new File(Path.userHome + "/.m2/repository").toURI.toString
}
