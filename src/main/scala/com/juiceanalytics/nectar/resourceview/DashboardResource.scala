package com.juiceanalytics.nectar.resourceview

import com.juiceanalytics.nectar.model.Dashboard
import javax.ws.rs._

/**
 * Is the {@link Dashboard} entity resource view.
 *
 * @author Jon Buffington
 */
@Path("dashboards")
trait DashboardResource {
  @GET
  @Path("{id}")
  @Produces(Array("application/vnd.juiceanalytics+json", "application/json"))
  def dashboard(@PathParam("id") id: Int): DashboardBean
}

/**
 * Is the default implementation of a {@link DashboardResource}.
 */
class DashboardResourceImpl extends DashboardResource {
  // curl -H "Accept: application/vnd.juiceanalytics+json" http://localhost:8080/resources/dashboards/1
  def dashboard(id: Int): DashboardBean = {
    val dashboard = Dashboard.findById(id)
    new DashboardBean(dashboard.getOrElse(new Dashboard))
  }
}

/**
 * Exposes the {@link Dashboard} as a Java bean to the Jackson encoder.
 */
case class DashboardBean(dashboard: Dashboard) {
  def getId = dashboard.id
  def getTitle = dashboard.title
  def getCreator = dashboard.creator
  def getCreated = dashboard.created
  def getModified = dashboard.modified
  def getTags = dashboard.tags
  def getViewers = dashboard.viewers
}
