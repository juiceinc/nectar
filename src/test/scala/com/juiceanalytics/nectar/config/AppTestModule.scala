package com.juiceanalytics.nectar.config

import com.google.appengine.api.users.UserService
import com.google.inject.AbstractModule
import com.juiceanalytics.nectar.resourceview.{UserResource, UserResourceImpl}
import com.juiceanalytics.nectar.security.{AuthenticatedContext, MockAuthenticatedContext}
import org.scalatest.mock.MockitoSugar


/**
 * Is the application Guice bindings used during unit tests.
 *
 * @author Jon Buffington
 */
class AppTestModule extends AbstractModule with MockitoSugar {
  import org.mockito.Mockito._
  import org.mockito.Matchers._

  lazy val userServiceMock: UserService = {
    val m = mock[UserService]
    when(m.createLogoutURL(anyString())).thenReturn("/logout")
    m
  }

  override protected def configure() {
    bind(classOf[AuthenticatedContext]).to(classOf[MockAuthenticatedContext])
    bind(classOf[UserService]).toInstance(userServiceMock)
    bind(classOf[UserResource]).to(classOf[UserResourceImpl])
  }
}
