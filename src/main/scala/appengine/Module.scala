package appengine

import com.google.appengine.api.users.{UserServiceFactory, UserService}
import com.google.inject.AbstractModule

/**
 * Is the Guice bindings for the GAE integration components.
 *
 * @author Jon Buffington
 */
class Module extends AbstractModule {

  override protected def configure() {
    bind(classOf[UserService]).toInstance(UserServiceFactory.getUserService)
  }
}
