package com.juiceanalytics.nectar

import org.junit.runner.RunWith
import org.scalatra.test.scalatest._
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers._

@RunWith(classOf[JUnitRunner])
class SimpleFilterTest extends ScalatraFunSuite with ShouldMatchers {
  addFilter(classOf[SimpleFilter], "/*")

  test("GET / returns status 200") {
    get("/") {
      status should equal (200)
    }
  }
}
