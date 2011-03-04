package com.juiceanalytics.nectar.security

import org.apache.shiro.subject.Subject
import org.apache.shiro.SecurityUtils
import org.scalatra.ScalatraKernel


/**
 * Is the application security model trait. This trait provides access to
 * authentication and authorization based upon both the Apache Shiro library
 * and the Google App Engine User service.
 *
 * @author Jon Buffington
 */
trait SecurityModel {
  this: ScalatraKernel =>

  /**
   * @return Returns either the Some(userId) or None.
   */
  def currentUser: Option[String] = {
    val principle = request.getUserPrincipal
    if (principle == null) {
      None
    }
    else {
      Some(principle.getName)
    }
  }

  /**
   * @return Returns the logout URL.
   */
  def logoutURL: String = "/logout"

  /**
   * @return Returns the current Subject.
   */
  def subject: Subject = SecurityUtils.getSubject
}
