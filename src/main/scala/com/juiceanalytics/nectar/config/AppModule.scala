package com.juiceanalytics.nectar.config

import com.google.inject.AbstractModule
import com.juiceanalytics.nectar.resourceview.UserResource
import com.juiceanalytics.nectar.security.{AuthenticatedContext, ShiroAuthenticatedContext}
import com.google.appengine.api.users.{UserServiceFactory, UserService}

/**
 * Are the application Guice bindings used during development and production modes.
 *
 * @author Jon Buffington
 */
class AppModule extends AbstractModule {

  override protected def configure() {
    bind(classOf[AuthenticatedContext]).to(classOf[ShiroAuthenticatedContext])
    bind(classOf[UserService]).toInstance(UserServiceFactory.getUserService)
    bind(classOf[UserResource])
  }
}
