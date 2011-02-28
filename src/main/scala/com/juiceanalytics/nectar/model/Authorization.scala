package com.juiceanalytics.nectar.model

/**
 * The Roles object contains the mapping of role constant names and their respective short names
 * used by Apache Shiro.
 *
 * @see http://manage.juiceanalytics.com/confluence/display/SLICE/Authorization+Roles+and+Permissions
 */
object Roles {
  val dashboardViewer = "viewer"
  val dashboardEditor = "editor"
  val clientAdmin = "client"
  val sysAdmin = "sysadmin"
}

/**
 * The Permissions object contains the mapping of permission constant names and their respective short names
 * used by Apache Shiro.
 *
 * @see http://manage.juiceanalytics.com/confluence/display/SLICE/Authorization+Roles+and+Permissions
 */
object Permissions {
  val createDashboard = "cd"
  val readDashboard = "rd"
  val updateDashboard = "ud"
  val deleteDashboard = "dd"
  val editDashboardPermissions = "perms"
  val uploadCSV = "upload"
  val createDashboardUser = "cu"
  val readDashboardUser = "ru"
  val updateDashboardUser = "uu"
  val deleteDashboardUser = "du"
  val modifyBillingInfo = "billing"
  val createClient = "ca"
  val readClient = "ra"
  val updateClient = "ua"
  val deleteClient = "da"
}
