package com.juiceanalytics.nectar.model

import com.googlecode.objectify._
import com.google.appengine.api.datastore._
import java.util.List
import java.util.ArrayList
import annotation.{Indexed, Unindexed}
import appengine._
import java.util.List


/**
 * Is the Entity class that models a Slice instance in a sliceboard.
 */
@Unindexed
case class Slice() extends KeyAsId  {

  import collection.mutable.ArrayBuffer
  import collection.JavaConversions._

  @Indexed      var sliceType      : String                 = ""
           var title          : String                 = ""
           var subtitle       : String                 = ""
           var index          : Int                    = 0


}


/**
 * Is the Slice entity companion object.
 */
object Slice extends DAO[Slice] {
  register()

  // TODO Remove after implementing Slice CRUD.
  def createTester():Slice ={
    val s = new Slice
    s.sliceType = "KeyMetrics"
    s.title = "Key Metric Slice"
    s.subtitle = "FOO SUB TITLE"
    s.index=0;
    save(s);
    return s;
  }

  /**
   * Find a Slice entity by type.
   *
   * @param sliceType Is the type of slice the user wants to view.
   * @return Returns either Some(Slice) or None.
   */
  def getBySliceType(sliceType: String): Option[Slice] =  {
    Option(ofy.query(classOf[Slice]).filter("sliceType", sliceType).get)
  }
         /**
   * An easy method to get all elements of a specific type of class.
   * @TODO needs to be updated to be sensitive to the 30s limit, by adding cursors.
   * @param entity the type of entity to look up.
   *
   */
  def getAll(): List[Slice] = {

    val query = ofy.query(classOf[Slice]);

    val iterator:QueryResultIterator [Slice]  = query.iterator()
    val resultSet = new ArrayList[Slice]

    while (iterator.hasNext()) {
      resultSet.add(iterator.next());
    }
     return resultSet
  }

}
