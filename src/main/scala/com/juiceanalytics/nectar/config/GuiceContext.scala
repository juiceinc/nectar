package com.juiceanalytics.nectar.config

import com.google.inject.Guice
import com.google.inject.servlet.GuiceServletContextListener
import com.juiceanalytics.nectar._

/**
 * Configures the injector for Guice configured servlets and filters.
 *
 * @author Jon Buffington
 */
class GuiceContext extends GuiceServletContextListener {
  def getInjector = Guice.createInjector(
    new appengine.Module,
    new resourceview.Module,
    new security.Module,
    new WebModule)
}
