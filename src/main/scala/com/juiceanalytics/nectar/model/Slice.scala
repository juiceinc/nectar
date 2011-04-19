package scala.com.juiceanalytics.nectar.model

import com.googlecode.objectify._
import annotation.{Indexed, Unindexed}
import appengine._

/**
 * Is the Entity class that models a Slice instance in a sliceboard.
 */
@Unindexed
case class Slice() extends KeyAsId  {

  import collection.mutable.ArrayBuffer
  import collection.JavaConversions._

  @Indexed var sliceType      : String                 = ""
           var title          : String                 = ""
           var subtitle       : String                 = ""
           var index          : Int                    = 0


}


/**
 * Is the user entity companion object.
 */
object Slice extends DAO[Slice] {
  register()

  // TODO Remove after implementing Slice CRUD.
  def createTester {
    val s = new Slice
    s.sliceType = "KeyMetrics"
    s.title = "Key Metric Slice"
    s.subtitle = "FOO SUB TITLE"
    save(s)
  }

  /**
   * Find a user entity by email address.
   *
   * @param email Is the email address of the entity to locate.
   * @return Returns either Some(User) or None.
   */
  def findBySliceType(sliceType: String): Option[Slice] =
    Option(ofy.query(classOf[Slice]).filter("sliceType", sliceType).get)
}
