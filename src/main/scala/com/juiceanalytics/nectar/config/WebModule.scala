package com.juiceanalytics.nectar.config

import com.google.inject.servlet.ServletModule
import com.juiceanalytics.nectar.AppFilter
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer

/**
 * Supplies the mappings for Guice annotated servlets and filters.
 */
class WebModule extends ServletModule {
  override def configureServlets() {
    import scala.collection.JavaConversions._

    filter("/*").through(classOf[AppFilter])
    serve("/resources/*").`with`(classOf[GuiceContainer], Map(
      "javax.ws.rs.Application" -> "com.juiceanalytics.nectar.RestApplication",
      "com.sun.jersey.api.json.POJOMappingFeature" -> "true"
    ))
  }
}
