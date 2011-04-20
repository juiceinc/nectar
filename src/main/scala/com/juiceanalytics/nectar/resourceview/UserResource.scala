package com.juiceanalytics.nectar.resourceview

import com.google.inject.Inject
import com.juiceanalytics.nectar.model.{Dashboard, User}
import com.juiceanalytics.nectar.security.AuthenticatedContext
import javax.ws.rs._


/**
 * Is the {@link User} model entity resource view.
 *
 * @author Jon Buffington
 */
@Path("users")
trait UserResource {
  @GET
  @Path("current")
  @Produces(Array("application/vnd.juiceanalytics+json", "application/json"))
  def current: UserBean

  @GET
  @Path("current/dashboards")
  @Produces(Array("application/vnd.juiceanalytics+json", "application/json"))
  def dashboards: java.util.Set[DashboardBean]

  @POST
  @Path("current/dashboards")
  @Consumes(Array("application/x-www-form-urlencoded"))
  @Produces(Array("text/plain"))
  def createDashboard(@FormParam("title") title:String, @FormParam("tags") tags:String, @FormParam("viewers") viewers:String): String
}

/**
 * Is the default implementation of a {@link UserResource}.
 */
class UserResourceImpl @Inject()(val authContext:AuthenticatedContext) extends UserResource {
  protected def user: User = authContext.currentUser.getOrElse(new User)

  def current: UserBean = new UserBean(user)

  // curl -H "Accept: application/vnd.juiceanalytics+json" http://localhost:8080/resources/users/current/dashboards
  def dashboards: java.util.Set[DashboardBean] = {
    import scala.collection.JavaConversions._

    if (authContext.currentUser.isDefined) {
      val userId = authContext.currentUser.get.id.asInstanceOf[scala.Long]
      Dashboard.findByUserId(userId).map(DashboardBean(_))
    }
    else {
      Set[DashboardBean]()
    }
  }

  // curl -d "title=Jonco&tags=a,b,c&viewers=1,2,3" http://localhost:8080/resources/users/current/dashboards
  def createDashboard(title: String, tags: String, viewers: String): String = {
    if (authContext.currentUser.isDefined) {
      import scala.collection.JavaConversions._

      val dashboard = new Dashboard
      dashboard.creator = authContext.currentUser.get.id
      if (title != null) {
        dashboard.title = title
      }
      if (tags != null) {
        for (tag <- tags.split(',')) {
          dashboard.tags.add(tag)
        }
      }
      if (viewers != null) {
        for (viewer <- viewers split (',')) {
          dashboard.viewers.add(viewer.toInt)
        }
      }
      Dashboard.save(dashboard)
      dashboard.id.toString
    }
    else {
      "not authenticated"
    }
  }
}

/**
 * Exposes the {@link User} as a Java bean to the Jackson encoder.
 */
case class UserBean(user: User) {
  def getId = user.id
  def getFirstName = user.firstName
  def getLastName = user.lastName
  def getEmail = user.email
  def getRoles = user.rolesNames
  def getIsEditor = user.isEditor
  def getIsClientAdmin = user.isClientAdmin
}
