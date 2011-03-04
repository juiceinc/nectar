package com.juiceanalytics.nectar

import com.google.appengine.api.users.UserServiceFactory
import grizzled.slf4j.Logger
import java.net.URL
import model.User
import org.scalatra.ScalatraFilter
import org.scalatra.fileupload.FileUploadSupport
import org.scalatra.scalate.ScalateSupport
import security.{SecuredResponder, SecurityModel}
import util.CSVParser
import org.apache.commons.fileupload.FileItem
import java.io.{BufferedReader, InputStreamReader}


class AppFilter extends ScalatraFilter
                        with SecurityModel
                        with SecuredResponder
                        with FileUploadSupport
                        with ScalateSupport {

  private lazy val logger = Logger(getClass)

  /**
   * @return Returns true if the current request is for the administrative
   * section of the application.
   */
  protected def isAdminReq: Boolean = requestPath.startsWith("/admin/")

  /**
   * Attempt to render a template if it exists. If the template does not exist,
   * the allow the filter chain processing to continue.
   */
  protected def useTemplate {
    val templateBase = requestPath match {
      case s if s.endsWith("/") => s + "index"
      case s => s
    }
    val templatePath = "/WEB-INF/scalate/templates" + templateBase + ".scaml"
    servletContext.getResource(templatePath) match {
      case _: URL =>
        logger.debug("Using template, " + templatePath + '.')
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

  override def logoutURL: String = {
    if (isAdminReq) {
      UserServiceFactory.getUserService.createLogoutURL("/admin/")
    }
    else {
      super.logoutURL
    }
  }

  // Provide a route to a common logout URL.
  get(super.logoutURL) {
    subject.logout
    redirect("/")
  }

  // Attempt to use templates before returning 404.
  notFound {
    useTemplate
  }

  // ------------------------------------------------------------------------
  // TODO Remove after enrollment workflow is set-up.
  // ------------------------------------------------------------------------
  get("/create-tester") {
    User.createTester.toString
  }

  // ------------------------------------------------------------------------
  // TODO Remove both "/data/" routes after data upload workflow is set-up.
  // ------------------------------------------------------------------------
  get("/data/") {
    <html>
      <body>
        <h1>File Upload</h1>
        <form method="post" enctype="multipart/form-data">
            <input name="csvfile" type="file"/>
            <input type="submit"/>{csrfElement}
        </form>
      </body>
    </html>
  }

  post("/data/") {
    // Grab the FileItem.
    processCSVFile(fileParams("csvfile"))
  }

/*
  def processCSVFile(fi: FileItem) {
    val parsed = CSVParser.parseFile(new InputStreamReader(fi.getInputStream))
    var n = 0
    val writer = response.getWriter
    for (rows <- parsed; row <- rows) {
      for (col <- row) {
        writer.print(col)
        writer.print(" | ")
      }
      writer.println()
      n += 1
    }
    writer.println("Parsed " + n + " rows.")

    response.setContentType("text/plain")
  }
*/

  def processCSVFile(fi: FileItem) {
    val writer = response.getWriter
    val reader = new BufferedReader(new InputStreamReader(fi.getInputStream))

    var line = reader.readLine
    var n = 0
    while (line != null) {
      val parsed = CSVParser.parseLine(line)
      for (record <- parsed; col <- record) {
        writer.print(col)
        writer.print(" | ")
      }
      writer.println()
      line = reader.readLine
      n += 1
    }

    writer.println("Parsed " + n + " rows.")

    response.setContentType("text/plain")
  }
}
