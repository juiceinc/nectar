package com.juiceanalytics.nectar.security

import com.juiceanalytics.nectar.model.User
import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject


/**
 * Is the authentication context implemented using Apache Shiro.
 *
 * @author Jon Buffington
 */
class ShiroAuthenticatedContext extends AuthenticatedContext {

  def currentUserId = {
    val s = subject
    val principal = s.getPrincipal
    if (s != null && s.getPrincipal != null) {
      Some(s.getPrincipal.asInstanceOf[String])
    }
    else {
      None
    }
  }

  def currentUser: Option[User] = for (id <- currentUserId; u <- User.findByEmail(id)) yield {
    u
  }

  def subject: Subject = SecurityUtils.getSubject
}
