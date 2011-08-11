package com.juiceanalytics.nectar.model

import com.googlecode.objectify.Key
import java.util.Date
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite


/**
 * Is the unit tests for the {@link Dashboard} model class.
 *
 * @author Jon Buffington
 */
@RunWith(classOf[JUnitRunner])
class DashboardTest extends FunSuite with ShouldMatchers {

  val dashboardFixture: Dashboard = new Dashboard
  val threeTags                   = List("tag1", "tag2", "tag3")

  override protected def withFixture(test: NoArgTest) {
    dashboardFixture.title = "Untitled"
    dashboardFixture.creator = 1
    dashboardFixture.created = new Date
    dashboardFixture.modified = new Date
    dashboardFixture.tags.clear()
    dashboardFixture.viewers.clear()
    test()
  }

  test("dashboard does not have duplicate tags") {
    dashboardFixture.tags.add("a")
    dashboardFixture.tags.add("b")
    dashboardFixture.tags.add("a")
    dashboardFixture.tags.size should be(2)
    dashboardFixture.tags should contain("a")
    dashboardFixture.tags should contain("b")
    dashboardFixture.tags.contains("c") should be(false)
  }

  test("dashboard does not have duplicate viewers") {
    import java.lang.Long

    dashboardFixture.viewers.add(1)
    dashboardFixture.viewers.add(2)
    dashboardFixture.viewers.add(1)
    dashboardFixture.viewers.size should be(2)
    dashboardFixture.viewers.contains(Long.valueOf(1)) should be(true)
    dashboardFixture.viewers.contains(Long.valueOf(2)) should be(true)
    dashboardFixture.viewers.contains(Long.valueOf(3)) should be(false)
  }
}
