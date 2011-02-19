package com.juiceanalytics.nectar

import java.net.URL
import org.scalatra._
import scalate.ScalateSupport

abstract class SecuredFilter extends ScalatraFilter with ScalateSupport with Security {

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
        templateEngine.layout(templatePath, Map("subject" -> subject))
      case _ =>
        filterChain.doFilter(request, response)
    }
  }
}
