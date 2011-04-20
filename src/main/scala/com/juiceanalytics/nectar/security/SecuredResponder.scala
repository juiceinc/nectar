package com.juiceanalytics.nectar.security

import org.scalatra.ScalatraKernel
import scala.xml.NodeSeq


/**
 * Is a trait that provides support for guarding servlets and filters
 * against known cross-site attacks. Currently, the protection is focused
 * on Cross Site Request Forgery (CSRF).
 *
 * @author Jon Buffington
 */
trait SecuredResponder extends CSRFTokenSupport {
  this: ScalatraKernel =>

  /**
   * Exclude App Engine URIs from CSRF inspections.
   */
  override protected def exclusions = super.exclusions ++ List("/_ah", "/resources")

  /**
   * @return Returns an input element for inserting the CSRF token into a form.
   */
  protected def csrfElement: NodeSeq = <input type="hidden" name={csrfKey} value={csrfToken}/>
}
