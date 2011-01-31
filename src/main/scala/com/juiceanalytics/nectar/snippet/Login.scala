package com.juiceanalytics.nectar.snippet

import net.liftweb.common.Loggable
import net.liftweb.http.{SHtml, S, SessionVar}
import net.liftweb.util._
import Helpers._


/**
 * A snippet that demonstrates and alternative to MegaProtoUser authentication.
 * The actual test could be handled by Apache Shiro, et al.
 */
object Login extends Loggable {

  // Use a StatefulSnippet to avoid using RequestVars.
  object isLoggedIn extends SessionVar[Boolean](false)

  def render: CssBindFunc = {
    // define some variables to put our values into
    var loginId = ""
    var password = ""

    // process the form
    def process() {
      // Only jon/b can successfully authenticate.
      val loginSuccess = loginId == "jon" && password == "b"
      logger.info("loginId = " + loginId + " / password = " + password)
      isLoggedIn.set(loginSuccess)
      if (loginSuccess) {
        S.notice("Login ID: " + loginId)
        S.redirectTo("/")
      }
      else {
        S.error("You suck.")
      }
    }

    //associate each of the form elements
    // with a function... behavior to perform when the
    // for element is submitted
    "name=loginid" #> SHtml.onSubmit(loginId = _) &
        "name=password" #> SHtml.onSubmit(password = _) &
        "type=submit" #> SHtml.onSubmitUnit(process)
  }
}
