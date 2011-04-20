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
           var rolesNames: java.util.List[String] = new ArrayBuffer[String]

  def isEditor: Boolean = rolesNames.contains(Roles.dashboardEditor)

  def isClientAdmin: Boolean = rolesNames.contains(Roles.clientAdmin)

  def credentials: String = hash64

  def salt: String = salt64

  def roles: Set[String] = Set(rolesNames: _*)

  def uniqueName: String = email

  override def toString: String = {
    "User{" +
        "id=" + id.toString +
        ", email=" + email +
        ", firstName=" + firstName +
        ", lastName=" + lastName +
        ", hash64=" + hash64 +
        ", salt64=" + salt64 +
        ", rolesNames=" + rolesNames.toString +
        '}'
  }
}


/**
 * Is the user entity companion object.
 */
object User extends DAO[User] {
  register()

  // TODO Remove after implementing user CRUD.
  def createTester(): User = {
    val u = new User
    u.email = "j@b.com"
    u.salt64 = UserEntity.salt
    u.hash64 = UserEntity.hashedCredentials("hello", u.salt64)
    u.firstName = "Jon"
    u.lastName = "Buffington"
    u.rolesNames.add(Roles.dashboardViewer)
    save(u)
    u
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
