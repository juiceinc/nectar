package com.juiceanalytics.nectar

import javax.ws.rs.core.Application
import resourceview.UserResource
import scala.collection._
import scala.collection.JavaConversions._


/**
 * Specifies the ReSTful resource views available to the API.
 *
 * @author Jon Buffington
 */
class RestApiApplication extends Application {
  override def getClasses = mutable.Set[java.lang.Class[_]](classOf[UserResource])
}

/**
 * Is the companion object used to provide API-wide values and functions.
 *
 * @author Jon Buffington
 */
object RestApiApplication {
  val JSON_MEDIA_TYPE = "application/vnd.juiceanalytics+json"
  val XML_MEDIA_TYPE = "application/vnd.juiceanalytics+xml"
}
