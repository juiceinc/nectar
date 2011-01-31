import sbt._

class NectarPlugins(info: ProjectInfo) extends PluginDefinition(info) {
  val jawsyMavenReleases = "Jawsy.fi M2 releases" at "http://oss.jawsy.fi/maven2/releases"
  lazy val jrebelPlugin = "fi.jawsy" % "sbt-jrebel-plugin" % "0.2.1"
  lazy val appenginePlugin    = "net.stbbs.yasushi" % "sbt-appengine-plugin" % "2.3-SNAPSHOT"
}
