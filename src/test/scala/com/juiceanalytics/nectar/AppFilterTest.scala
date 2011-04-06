package com.juiceanalytics.nectar

import com.google.inject.Guice
import config.AppTestModule
import org.junit.runner.RunWith
import org.scalatra.test.scalatest._
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers._


@RunWith(classOf[JUnitRunner])
class AppFilterTest extends ScalatraFunSuite with ShouldMatchers  {

  val injector = Guice.createInjector(new AppTestModule)

  addFilter(injector.getInstance(classOf[AppFilter]), "/*")

  test("GET / returns status 200") {
    get("/") {
      status should equal(200)
    }
  }

  test("GET /logout returns status 302") {
    get("/logout") {
      status should equal(302)
    }
  }

  test("GET /admin/ returns status 200") {
    get("/admin/") {
      status should equal(200)
    }
  }

}
