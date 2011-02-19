package com.juiceanalytics.nectar

import org.apache.shiro.subject.Subject

/**
 * Is the application security model trait. This trait provides access to
 * authentication and authorization based upon the Apache Shiro library.
 *
 * @author Jon Buffington
 */
trait Security {
  def subject: Subject
}
