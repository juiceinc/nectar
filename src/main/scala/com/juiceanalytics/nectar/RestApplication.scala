package com.juiceanalytics.nectar

import javax.ws.rs.core.Application
import resourceview.{UserResource,SliceResource}

import scala.collection._
import scala.collection.JavaConversions._


/**
 * Specifies the root resource views deployed under the "/resources" URI.
 *
 * @author Jon Buffington
 */
class RestApplication extends Application {
  override def getClasses = mutable.Set[java.lang.Class[_]](classOf[UserResource],classOf[SliceResource])
}

/**
 * Is the companion object used to provide API-wide values and functions.
 *
 * @author Jon Buffington
 */
object RestApplication {
  val JSON_MEDIA_TYPE = "application/vnd.juiceanalytics+json"
  val XML_MEDIA_TYPE = "application/vnd.juiceanalytics+xml"
}
