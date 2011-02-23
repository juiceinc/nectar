package com.juiceanalytics.nectar.model

import com.googlecode.objectify._
import annotation.{Indexed, Unindexed}
import appengine._


@Unindexed
case class User() extends KeyAsId with UserEntity {
  @Indexed var email: String = ""
  var firstName: String = ""
  var lastName: String = ""
  var hash64: String = ""
  var salt64:String = ""

  def credentials: String = hash64

  def salt: String = salt64

  def roles: Set[String] = Set("admin")

  def uniqueName: String = email
}


object User extends DAO[User] {
  register()

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
