package com.juiceanalytics.nectar.model

import com.googlecode.objectify._
import annotation.{Indexed, Unindexed}
import appengine._

/**
 * Is the Entity class that models dashboard users and client administrators.
 */
@Unindexed
case class User() extends KeyAsId with UserEntity {

  import collection.mutable.ArrayBuffer
  import collection.JavaConversions._

  @Indexed var email     : String                 = ""
           var firstName : String                 = ""
           var lastName  : String                 = ""
           var hash64    : String                 = ""
           var salt64    : String                 = ""
           var rolesNames: java.util.List[String] = ArrayBuffer(Roles.dashboardViewer)

  def isEditor: Boolean = rolesNames.contains(Roles.dashboardViewer, Roles.dashboardEditor)

  def isClientAdmin: Boolean = rolesNames.contains(Roles.clientAdmin)

  def credentials: String = hash64

  def salt: String = salt64

  def roles: Set[String] = Set(rolesNames: _*)

  def uniqueName: String = email
}


/**
 * Is the user entity companion object.
 */
object User extends DAO[User] {
  register()

  // TODO Remove after implementing user CRUD.
  def createTester {
    val u = new User
    u.email = "j@b.com"
    u.salt64 = UserEntity.salt
    u.hash64 = UserEntity.hashedCredentials("hello", u.salt64)
    u.firstName = "Jon"
    u.lastName = "Buffington"
    save(u)
  }

  /**
   * Find a user entity by email address.
   *
   * @param email Is the email address of the entity to locate.
   * @return Returns either Some(User) or None.
   */
  def findByEmail(email: String): Option[User] =
    Option(ofy.query(classOf[User]).filter("email", email).get)
}
