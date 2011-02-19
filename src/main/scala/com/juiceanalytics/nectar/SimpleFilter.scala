package com.juiceanalytics.nectar

import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject

class SimpleFilter extends SecuredFilter {
  def subject: Subject = SecurityUtils.getSubject
}
