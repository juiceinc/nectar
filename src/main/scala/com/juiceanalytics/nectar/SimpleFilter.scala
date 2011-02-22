package com.juiceanalytics.nectar

import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject
import com.google.appengine.api.users.UserServiceFactory

class SimpleFilter extends SecuredFilter {
  def currentUser: Option[String] = {
    if (isAdminReq)
    val principle = request.getUserPrincipal
    if (principle == null)
      None
    else
      Some(principle.getName)
  }

  def logoutURL: String = {
    if (isAdminReq)
      UserServiceFactory.getUserService.createLogoutURL("/admin/")
    else
      "/logout"
  }

  def subject: Subject = SecurityUtils.getSubject
}
