import sbt._

class NectarPlugins(info: ProjectInfo) extends PluginDefinition(info) {
  val appenginePlugin    = "net.stbbs.yasushi" % "sbt-appengine-plugin" % "2.1" from "http://github.com/downloads/Yasushi/sbt-appengine-plugin/sbt-appengine-plugin-2.1.jar"
  val jawsyMavenReleases = "Jawsy.fi M2 releases" at "http://oss.jawsy.fi/maven2/releases"
  lazy val jrebelPlugin = "fi.jawsy" % "sbt-jrebel-plugin" % "0.2.1"
}
