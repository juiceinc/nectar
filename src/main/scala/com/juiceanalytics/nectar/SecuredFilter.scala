package com.juiceanalytics.nectar

import java.net.URL
import org.scalatra._
import scalate.ScalateSupport
import xml.NodeSeq

abstract class SecuredFilter extends ScalatraFilter
                                     with ScalateSupport
                                     with Security
                                     with security.CSRFTokenSupport
{
  /**
   * @return Returns true if the current request is for the administrative
   * section of the application.
   */
  protected def isAdminReq: Boolean = requestPath.startsWith("/admin/")

  /**
   * @return Returns an input element for inserting the CSRF token into a form.
   */
  protected def csrfElement:NodeSeq = <input type="hidden" name={csrfKey} value={csrfToken}/>

  get("/logout") {
    subject.logout
    redirect("/")
  }

  notFound {
    // If no route matches, then try to render a Scaml template
    val templateBase = requestPath match {
      case s if s.endsWith("/") => s + "index"
      case s => s
    }
    val templatePath = "/WEB-INF/scalate/templates" + templateBase + ".scaml"
    servletContext.getResource(templatePath) match {
      case url: URL =>
        contentType = "text/html"
        val attributes = Map(
          "csrfElement" -> csrfElement,
          "currentUser" -> currentUser,
          "logoutURL" -> logoutURL
        )
        templateEngine.layout(templatePath, attributes)
      case _ =>
        filterChain.doFilter(request, response)
    }
  }
}
