package com.juiceanalytics.nectar

import org.apache.shiro.subject.Subject

/**
 * Is the application security model trait. This trait provides access to
 * authentication and authorization based upon both the Apache Shiro library
 * and the Google App Engine User service.
 *
 *
 * @author Jon Buffington
 */
trait Security {
  /**
   * @return Returns either the Some(userId) or None.
   */
  def currentUser: Option[String]

  /**
   * @return Returns the logout URL.
   */
  def logoutURL: String

  /**
   * @return Returns the current Subject.
   */
  def subject: Subject
}
