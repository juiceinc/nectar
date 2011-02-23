package com.juiceanalytics.nectar.security

import javax.servlet.{ServletResponse, ServletRequest}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter
import org.apache.shiro.web.util.WebUtils
import org.scalatra.ScalatraKernel


/**
 * Requires the requesting user to be authenticated for the request to continue, and if they are not, forces the user
 * to login via by redirecting them to the {@link #setLoginUrl(String) loginUrl}. The login form submission
 * verifies that the session CSRF token matches the form CSRF token.
 *
 * @author Jon Buffington
 */
class CSRFTokenAuthenticationFilter extends FormAuthenticationFilter {
  val csrfKey: String = ScalatraKernel.csrfKey

  /**
   * @return Returns the CSRF token value from the request.
   */
  def sessionToken(request: ServletRequest): Option[String] = {
    val session = request.asInstanceOf[HttpServletRequest].getSession(false)
    if (session == null) {
      None
    }
    else {
      val token = session.getAttribute(csrfKey)
      if (token == null) {
        None
      }
      else {
        Some(token.toString)
      }
    }
  }

  /**
   * @return Returns the CSRF token value from the form parameters.
   */
  def formToken(request: ServletRequest): Option[String] = {
    val token = WebUtils.getCleanParam(request, csrfKey)
    if (token == null) {
      None
    }
    else {
      Some(token)
    }
  }

  /**
   * Returns whether the request form token matches the request
   * session token. Since CSRF tokens are mandatory for login processing,
   * both tokens must exist otherwise false is returned.
   */
  def reqTokensMatch(request: ServletRequest): Boolean = {
    val stok = sessionToken(request)
    val ftok = formToken(request)
    stok.isDefined && ftok.isDefined && stok == ftok
  }

  override def onAccessDenied(request: ServletRequest, response: ServletResponse): Boolean = {
    if (isLoginRequest(request, response) && isLoginSubmission(request, response)) {
      if (reqTokensMatch(request)) {
        executeLogin(request, response)
      }
      else {
        response.asInstanceOf[HttpServletResponse].sendError(403, "Request tampering detected.")
        false
      }
    }
    else {
      super.onAccessDenied(request, response)
    }
  }
}
