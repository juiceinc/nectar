import sbt._
import java.io.File
import fi.jawsy.sbtplugins.jrebel.JRebelWebPlugin
import org.fusesource.scalate.sbt.PrecompilerWebProject

class NectarProject(info: ProjectInfo) extends AppengineProject(info) with JRebel
                                          with JRebelWebPlugin
                                          with PrecompilerWebProject
{
  // Add unchecked to the Scala compiler options.
  override def compileOptions = super.compileOptions ++ Seq(Unchecked)

  // http://groups.google.com/group/simple-build-tool/msg/1f17b43807d06cda
  override def testClasspath = super.testClasspath +++ buildCompilerJar

  // Allow connections from the source debugger to the App Engine dev instance.
  override def devAppserverJvmOptions:Seq[String] = List("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005") ++ super.devAppserverJvmOptions

  // Precompile the Scalate templates before updating the WEB-INF directory.
  override def precompileTemplatesAction = super.precompileTemplatesAction dependsOn(prepareWebapp)
  override def devAppserverStartAction = task { args => super.devAppserverStartTask(args) dependsOn(precompileTemplates) }
  override def updateWebappAction = task{ opts => appcfgTask("update", None, opts) dependsOn(precompileTemplates) } describedAs("Create or update an app version.")

  // TODO The following patch can be removed after sbt-scalate-plugin 1.4.1 is released.
  // See http://groups.google.com/group/scalate/browse_thread/thread/67334831863011f6
  override def scalateSources = super.scalateSources ** new FileFilter {
    def accept(file: java.io.File) = {
      List(webappPath, mainResourcesPath) find { _.asFile == file } isDefined
    }
  }

  // Add Maven Local repository for SBT to search for (disable if this doesn't suit you)
  val mavenLocal = "Local Maven Repository" at new File(Path.userHome + "/.m2/repository").toURI.toString
}
