package com.juiceanalytics.nectar.resourceview

import com.google.appengine.tools.development.testing._
import com.google.inject.Guice
import com.juiceanalytics.nectar.config.AppTestModule
import com.juiceanalytics.nectar.security.MockAuthenticatedContext
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite}


/**
 * Is the unit test cases for the {@link UserResource} class.
 *
 * @author Jon Buffington
 */
@RunWith(classOf[JUnitRunner])
class UserResourceTest extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
  val helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig())

  override def beforeEach() {
    helper.setUp()
  }

  override def afterEach() {
    helper.tearDown()
  }

  val injector     = Guice.createInjector(new AppTestModule)
  val userResource = injector.getInstance(classOf[UserResource])

  test("current UserResource is the mock User") {
    val userBean = userResource.current
    userBean.getId should be(null)
    userBean.getEmail should be(MockAuthenticatedContext.userId)
    userBean.getFirstName should be(MockAuthenticatedContext.firstName)
    userBean.getLastName should be(MockAuthenticatedContext.lastName)
    userBean.getRoles should contain(MockAuthenticatedContext.role)
    userBean.getIsEditor should be(false)
    userBean.getIsClientAdmin should be(false)
  }

  test("current user dashboards should be empty") {
    val dashboards = userResource.dashboards
    dashboards.size should be(0)
  }

  test("creating a Dashboard should return a mock instance id") {
    val result = userResource.createDashboard("title", "a,b,c", "1,2,3")
    result should be("1")
  }
}
