package com.juiceanalytics.nectar.security

import org.scalatest.mock.MockitoSugar
import com.juiceanalytics.nectar.model.{Roles, User}

/**
 * Is a mock authenticated context used by unit tests to simulate
 * authentication states.
 *
 * @author Jon Buffington
 */
class MockAuthenticatedContext extends AuthenticatedContext with MockitoSugar {
  import org.apache.shiro.subject.Subject
  import org.mockito.Mockito._

  lazy val mockedSubject: Subject = {
    val s = mock[Subject]
    when(s.isAuthenticated).thenReturn(true)
    s
  }

  def subject = mockedSubject

  def currentUser =  {
    val user = new User
    user.email = MockAuthenticatedContext.userId
    user.firstName = MockAuthenticatedContext.firstName
    user.lastName = MockAuthenticatedContext.lastName
    user.rolesNames.add(MockAuthenticatedContext.role)
    Some(user)
  }

  def currentUserId = Some(currentUser.get.email)
}

object MockAuthenticatedContext {
  def userId = "j@b.com"
  def firstName = "Jon"
  def lastName = "Buffington"
  def role = Roles.dashboardViewer
}
