package com.juiceanalytics.nectar.resourceview

import com.google.inject.Guice
import com.juiceanalytics.nectar.config.AppTestModule
import com.juiceanalytics.nectar.security.MockAuthenticatedContext
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers


/**
 * Is the unit test cases for the {@link UserResource} class.
 *
 * @author Jon Buffington
 */
@RunWith(classOf[JUnitRunner])
class UserResourceTest extends FunSuite with ShouldMatchers {
  val injector = Guice.createInjector(new AppTestModule)

  test("current UserResource is the mock User") {
    val userResource = injector.getInstance(classOf[UserResource])
    val userBean = userResource.current
    userBean.getId should be(null)
    userBean.getEmail should be(MockAuthenticatedContext.userId)
    userBean.getFirstName should be(MockAuthenticatedContext.firstName)
    userBean.getLastName should be(MockAuthenticatedContext.lastName)
    userBean.getRoles should contain(MockAuthenticatedContext.role)
    userBean.getIsEditor should be(false)
    userBean.getIsClientAdmin should be(false)
  }
}
