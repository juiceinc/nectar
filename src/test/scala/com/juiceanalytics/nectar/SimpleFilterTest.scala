package com.juiceanalytics.nectar

import org.junit.runner.RunWith
import org.scalatra.test.scalatest._
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers._
import org.scalatest.mock.MockitoSugar


@RunWith(classOf[JUnitRunner])
class SimpleFilterTest extends ScalatraFunSuite with ShouldMatchers {

  addFilter(classOf[TestableSimpleFilter], "/*")

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
}

class TestableSimpleFilter extends SecuredFilter with MockitoSugar {
  import org.apache.shiro.subject.Subject
  import org.mockito.Mockito._

  lazy val mockedSubject: Subject = {
    val s = mock[Subject]
    when(s.isAuthenticated).thenReturn(false)
    s
  }

  override def subject: Subject = mockedSubject
}
