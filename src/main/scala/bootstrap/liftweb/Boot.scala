package bootstrap.liftweb

import _root_.net.liftweb.common._
import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.sitemap._
import Loc._
import Helpers._
import com.juiceanalytics.nectar.model.User
import com.juiceanalytics.nectar.snippet.Login

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  val AltAuthRequired = If(() => Login.isLoggedIn.get, () => RedirectResponse("/login"))

  def boot {
    // Specify where to search for snippet code.
    LiftRules.addToPackages("com.juiceanalytics.nectar")

    // Build the SiteMap.
    val siteMap = SiteMap(
      Menu.i("Home") / "index" >> User.AddUserMenusAfter,
      Menu.i("Text") / "text" >> EarlyResponse(() => Full(RedirectResponse("static/text.txt"))),
      Menu.i("Alt Login") / "login" >> User.loginFirst,
      Menu.i("Secure") / "secure" >> AltAuthRequired,
      Menu.i("Static") / "static" / ** >> Hidden
    )

    // Set the sitemap.  Note if we don't want access control for
    // based on MegaProtoUser, just use LiftRules.setSiteMap(siteMap) instead.
    LiftRules.setSiteMapFunc(() => User.sitemapMutator(siteMap))

    // Show the spinny image when an Ajax call starts.
    LiftRules.ajaxStart =
        Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends.
    LiftRules.ajaxEnd =
        Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8.
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))
  }
}
