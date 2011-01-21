import sbt._

class NectarProject(info: ProjectInfo) extends DefaultWebProject(info) {
  // If you're using JRebel for Lift development, uncomment this line.
  override def scanDirectories = Nil

  // Add Maven Local repository for SBT to search for (disable if this doesn't suit you)
  val mavenLocal = "Local Maven Repository" at "file://" + Path.userHome + "/.m2/repository"
}
