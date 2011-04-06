package com.juiceanalytics.nectar

import com.google.inject.Guice
import com.google.inject.servlet.GuiceServletContextListener
import config.{AppModule, FilterModule}

/**
 * Configures the injector for Guice configured servlets and filters.
 *
 * @author Jon Buffington
 */
class GuiceContextListener extends GuiceServletContextListener {
  def getInjector = Guice.createInjector(new AppModule, new FilterModule)
}
