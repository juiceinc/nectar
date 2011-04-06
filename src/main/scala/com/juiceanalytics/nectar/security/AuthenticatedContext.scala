package com.juiceanalytics.nectar.security

import com.juiceanalytics.nectar.model.User
import org.apache.shiro.subject.Subject


/**
 * Is the application authentication context trait. This trait provides access to
 * authentication and authorization based upon both the Apache Shiro library
 * and the Google App Engine User service.
 *
 * @author Jon Buffington
 */
trait AuthenticatedContext {
  /**
   * @return Returns either the Some(userId) or None.
   */
  def currentUserId: Option[String]

  /**
   * @return Returns either the Some(user entity) or None.
   */
  def currentUser: Option[User]

  /**
   * @return Returns the logout URL.
   */
  def logoutURL: String = "/logout"

  /**
   * @return Returns the current {@link Subject}.
   */
  def subject: Subject
}
