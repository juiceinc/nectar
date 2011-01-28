import sbt._
import fi.jawsy.sbtplugins.jrebel.JRebelWebPlugin

class NectarProject(info: ProjectInfo) extends AppengineProject(info) with JRebel with JRebelWebPlugin {
  // Add Maven Local repository for SBT to search for (disable if this doesn't suit you)
  val mavenLocal = "Local Maven Repository" at "file://" + Path.userHome + "/.m2/repository"
}
