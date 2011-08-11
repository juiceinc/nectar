package com.juiceanalytics.nectar.security

import scala.xml.NodeSeq
import org.scalatra.{CsrfTokenSupport, ScalatraKernel}

/**
 * Is a trait that provides support for guarding servlets and filters
 * against known cross-site attacks. Currently, the protection is focused
 * on Cross Site Request Forgery (CSRF).
 *
 * @author Jon Buffington
 */
trait SecuredResponder extends CsrfTokenSupport {
  this: ScalatraKernel =>

  /**
   * Exclude App Engine URIs from CSRF inspections.
   */
  protected def exclusions = List("/_ah", "/resources")

  /**
   * Tests whether a POST request is a potential cross-site forgery.
   */
  override protected def isForged: Boolean = {
    if (request.isWrite) {
      if (exclusions.exists(requestPath.startsWith(_))) {
        false
      }
      else {
        session.get(csrfKey) != params.get(csrfKey)
      }
    }
    else {
      false
    }
  }
  
  /**
   * @return Returns an input element for inserting the CSRF token into a form.
   */
  protected def csrfElement: NodeSeq = <input type="hidden" name={csrfKey} value={csrfToken}/>
}
