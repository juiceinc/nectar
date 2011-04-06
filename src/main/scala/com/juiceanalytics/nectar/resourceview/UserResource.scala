package com.juiceanalytics.nectar.resourceview

import com.google.inject.Inject
import com.juiceanalytics.nectar.model.User
import com.juiceanalytics.nectar.security.AuthenticatedContext
import javax.ws.rs.{GET, Path, Produces}


/**
 * Is the {@link User} model entity resource view.
 *  
 * @author Jon Buffington
 */
@Path("/api/user")
class UserResource @Inject()(val authContext:AuthenticatedContext) {

  @GET
  @Path("/current")
  @Produces(Array("application/vnd.juiceanalytics+json", "application/json"))
  def current: UserBean = {
    new UserBean(authContext.currentUser.getOrElse(new User))
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
