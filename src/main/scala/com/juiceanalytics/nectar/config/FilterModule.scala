package com.juiceanalytics.nectar.config

import com.google.inject.servlet.ServletModule
import com.juiceanalytics.nectar.AppFilter


/**
 * Supplies the mappings for Guice annotated servlets and filters.
 */
class FilterModule extends ServletModule {
  override def configureServlets() {
    filter("/*").through(classOf[AppFilter])
  }
}
