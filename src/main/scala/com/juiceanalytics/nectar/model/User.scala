package com.juiceanalytics.nectar.model

import _root_.net.liftweb.record._
import _root_.net.liftweb.common._
import net.liftweb.util.FieldError
import net.liftweb.sitemap.Menu
import xml.Node


/**
 * Create the global User meta-object. This object provides functions
 * across all instances.
 *
 * <p>The only current user is "sparky@dog.com" with password "woof"</p>
 */
object User extends User with MetaMegaProtoUser[User] with Loggable {
  //
  lazy val sparky = new User {
    firstName("Sparky")
    lastName("TheDog")
    email("sparky@dog.com")
    password("woof")
    validated(true)
  }

  protected def findUserByUserName(email: String): Box[User] = {
    logger.info("--> findUserByUserName")
    Full(sparky)
  }

  protected def findUserByUniqueId(id: String): Box[User] = {
    logger.info("--> findUserByUniqueId")
    Full(sparky)
  }

  protected def userFromStringId(id: String): Box[User] = {
    logger.info("--> userFromStringId")
    Full(sparky)
  }

  /**
   * Disable the sign-up/enrollment process.
   */
  override def createUserMenuLoc: Box[Menu] = Empty

  /**
   * Surround the login page with the default template.
   */
  override def screenWrap: Box[Node] = Full(<lift:surround with="default" at="content">
      <lift:bind/>
  </lift:surround>)
}

/**
 *  Is the class that defines any additional fields for User instances.
 */
class User extends MegaProtoUser[User] {
  def meta: MetaRecord[User] = User

  // TODO Validate that the email address is unique will be implemented in the Record adapter.
  protected def valUnique(errorMsg: => String)(email: String): List[FieldError] = List()
}
