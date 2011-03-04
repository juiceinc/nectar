package com.juiceanalytics.nectar.model

import com.googlecode.objectify._
import annotation.{Serialized, Unindexed}
import appengine._


/**
 * Is the container for a dashboard's data.
 *
 * @author Jon Buffington
 */
@Unindexed
case class DataSet() extends KeyAsId {

  import scala.collection.JavaConversions._

  //  @Indexed var owner: Key[Sliceboard] = _
  var title: String = "Untitled"

  var columns: java.util.ArrayList[java.lang.String] = new java.util.ArrayList[java.lang.String]()

  type RowType = java.util.ArrayList[java.lang.String]

  @Serialized
  var rows: java.util.ArrayList[RowType] = new java.util.ArrayList[RowType]()

  def columnCount: Int = columns.size

  def rowCount: Int = rows.size

  def apply(name: String, table: List[List[String]]) {
    title = name
    // Capture the header
    columns.clear
    val header = table(0)
    header.copyToBuffer(columns)
    // Capture the data
    rows.clear
    val data = table.tail
    for (row <- data) {
      rows.add(new RowType(row))
    }
  }
}


/**
 * Is the data set entity companion object.
 */
object DataSet extends DAO[DataSet] {
  register()
}
