package com.juiceanalytics.nectar.resourceview

import com.google.inject.AbstractModule

/**
 * Is the Guice bindings for the resourceview package.
 *
 * @author Jon Buffington
 */
class Module extends AbstractModule {

  override protected def configure() {
    bind(classOf[UserResource]).to(classOf[UserResourceImpl])
  }
}
