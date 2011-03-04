package com.juiceanalytics.nectar.model

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import scala.collection.JavaConversions._


@RunWith(classOf[JUnitRunner])
class DataSetTest extends FunSuite with ShouldMatchers {

  val dataset = new DataSet
  val header  = List("A", "B", "C")
  val row1    = List("a", "b", "c")
  val row2    = List("x", "y", "z")
  val table   = List(header, row1, row2)

  override protected def withFixture(test: NoArgTest) {
    // Reset the test fixture before executing any of the tests.
    dataset.title = "Untitled"
    dataset.columns.clear
    dataset.rows.clear

    test()
  }

  test("data set has columns") {
    dataset("testable", table)
    dataset.columnCount should be(3)
    dataset.columns(0) should be(header(0))
    dataset.columns(1) should be(header(1))
    dataset.columns(2) should be(header(2))
  }

  test("data set has rows") {
    dataset("testable", table)
    dataset.rowCount should be(2)
    dataset.rows(0) should have size(3)
    dataset.rows(0) should not be(row1)
    dataset.rows(1) should have size(3)
    dataset.rows(1) should not be(row2)
  }
}
