package com.juiceanalytics.nectar.config

import com.google.appengine.api.users.UserService
import com.google.inject.AbstractModule
import com.juiceanalytics.nectar.resourceview.UserResource
import com.juiceanalytics.nectar.security.{AuthenticatedContext, MockAuthenticatedContext}
import org.scalatest.mock.MockitoSugar


/**
 * Are the application Guice bindings used during development and production modes.
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
    bind(classOf[UserResource])
  }
}
