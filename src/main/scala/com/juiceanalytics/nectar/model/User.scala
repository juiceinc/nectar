package com.juiceanalytics.nectar.model

import xml.Node
import net.liftweb.common.{Box, Full}


/**
 * Create the global User meta-object. This object shares functions
 * across all instances of User.
 *
 * @author Jon Buffington
 */
object User extends User with appengine.MetaMegaProtoUser[User] {

  /**
   * Is the "from" email address for *ProtoUser generated email messages.
   */
  override def emailFrom: String = "no-reply@sliceboard.appspotmail.com"

  /**
   * Disable the sign-up/enrollment process.
   */
//  override def createUserMenuLoc: Box[Menu] = Empty

  /**
   * Surround the login page with the default template.
   */
  override def screenWrap: Box[Node] = Full(<lift:surround with="default" at="content">
      <lift:bind/>
  </lift:surround>)

  /**
   * After successful authentication, send the person to the App page.
   */
  override def homePage: String = "/app"
}

/**
 * Is the class that defines any additional fields for User instances.
 *
 * @author Jon Buffington
 */
class User extends appengine.MegaProtoUser[User] {
  def meta = User
}
