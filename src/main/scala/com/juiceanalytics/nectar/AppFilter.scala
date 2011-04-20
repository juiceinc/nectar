package com.juiceanalytics.nectar

import com.google.appengine.api.users.UserService
import com.google.inject.Inject
import com.google.inject.Singleton
import grizzled.slf4j.Logger
import java.io.InputStreamReader
import java.net.URL
import model.{DataSet, User, Slice}
import org.apache.commons.fileupload.{FileItemFactory, FileItem}
import org.apache.shiro.subject.Subject
import org.scalatra.ScalatraFilter
import org.scalatra.fileupload.FileUploadSupport
import org.scalatra.scalate.ScalateSupport
import security.{AuthenticatedContext, SecuredResponder}
import util.{MemoryFileItemFactory, CSVParser}


@Singleton
class AppFilter @Inject()(val authDelegate: AuthenticatedContext,
                          val userService: UserService) extends ScalatraFilter
                                                                with AuthenticatedContext
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
  protected def useTemplate() {
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
          "currentUserId" -> currentUserId,
          "logoutURL" -> logoutURL
        )
        templateEngine.layout(templatePath, attributes)
      case _ =>
        filterChain.doFilter(request, response)
    }
  }

  def currentUserId: Option[String] = {
    val principle = request.getUserPrincipal
    if (principle == null) {
      None
    }
    else {
      Some(principle.getName)
    }
  }

  def currentUser: Option[User] = authDelegate.currentUser

  override def logoutURL: String = {
    if (isAdminReq) {
      userService.createLogoutURL("/admin/")
    }
    else {
      authDelegate.logoutURL
    }
  }

  def subject: Subject = authDelegate.subject

  /**
   * Store uploaded files in memory only as App Engine prohibits file system access.
   */
  override protected def fileItemFactory: FileItemFactory = new MemoryFileItemFactory

  // Provide a route to a common logout URL.
  get(super.logoutURL) {
    subject.logout()
    redirect("/")
  }

  // Attempt to use templates before returning 404.
  notFound {
    useTemplate()
  }

  // ------------------------------------------------------------------------
  // TODO Remove after enrollment workflow is set-up.
  // ------------------------------------------------------------------------
  get("/create-tester") {
    User.createTester.toString
    Slice.createTester.toString
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

  def processCSVFile(fi: FileItem) {
    import collection.JavaConversions._

    val parsed = CSVParser.parseFile(new InputStreamReader(fi.getInputStream))

    // Save the data to the data store.
    val dataSet = new DataSet
    parsed.foreach {
      data: List[List[String]] =>
        dataSet("test", data)
        DataSet.save(dataSet)
    }

    // Output the data set back to the browser.
    val writer = response.getWriter
    def writeLn(row: java.util.List[String]) {
      for (col <- row) {
        writer.print(col)
        writer.print(" | ")
      }
      writer.println()
    }

    writeLn(dataSet.columns)
    for (row <- dataSet.rows) {
      writeLn(row)
    }

    writer.println("Parsed " + dataSet.rowCount + " rows.")

    response.setContentType("text/plain")
  }
}
