package com.juiceanalytics.nectar.security

import com.google.inject.AbstractModule

/**
 * Is the Guice bindings for the security package.
 *
 * @author Jon Buffington
 */
class Module extends AbstractModule {

  override protected def configure() {
    bind(classOf[AuthenticatedContext]).to(classOf[ShiroAuthenticatedContext])
  }
}
