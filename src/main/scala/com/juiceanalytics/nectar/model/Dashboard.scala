package com.juiceanalytics.nectar.model

import appengine._
import com.googlecode.objectify.annotation.{Indexed, Unindexed}
import java.util.Date


/**
 * Is the model class for a dashboard.
 *
 * @author Jon Buffington
 */
@Unindexed
case class Dashboard() extends KeyAsId {
  var title   : String                          = ""
  @Indexed
  var creator : java.lang.Long                  = _
  var created : Date                            = new Date
  var modified: Date                            = new Date
  var tags    : java.util.Set[java.lang.String] = new java.util.HashSet[java.lang.String]
  @Indexed
  var viewers : java.util.Set[java.lang.Long]   = new java.util.HashSet[java.lang.Long]

  override def toString: String = {
    "Dashboard{" +
        "id=" + id.toString +
        ", title=" + title +
        ", creator=" + creator.toString +
        ", created=" + created.toString +
        ", modified=" + modified.toString +
        ", tags=" + tags.toString +
        ", viewers=" + viewers.toString +
        '}'
  }
}

/**
 * Is the Dashboard companion object.
 */
object Dashboard extends DAO[Dashboard] {
  register()

  /**
   * Find all writable and read-only dashboards for a given user Id.
   *
   * @return Return zero-or-more Dashboard instances where the userKey
   * matches the creator or is in the viewers set.
   */
  def findByUserId(userId: Long): Set[Dashboard] = {
    import scala.collection.JavaConversions._

    // TODO Handle the case where a large number of dashboard entity could be fetched.
    val creatorKeys = ofy.query(classOf[Dashboard]).filter("creator", userId).list();
    val viewerKeys = ofy.query(classOf[Dashboard]).filter("viewers", userId).list();
    creatorKeys ++ viewerKeys toSet
  }
}
