package appengine

import com.google.appengine.api.datastore._
import net.liftweb.record._
import net.liftweb.common._

/**
 * Is a trait that aids with storing and retrieving derived records using
 * Google App Engine datastore entities.
 *
 * @author Jon Buffington
 */
trait AppEngineRecord[BaseRecord <: AppEngineRecord[BaseRecord]] extends Record[BaseRecord] {
  this: BaseRecord =>

  /**
   * The meta data for this record.
   */
  def meta: AppEngineMetaRecord[BaseRecord]

  /**
   * Provide access to the datastore instance.
   */
  protected def datastore: DatastoreService = DatastoreServiceFactory.getDatastoreService

  /**
   * Is the Entity kind for the record.
   */
  def entityKind: String = getClass.getName

  /**
   * Is the App Engine entity key.
   */
  protected[appengine] var key: Box[Key] = Empty

  /**
   * Hold an optional parent entity.
   */
  protected[appengine] var parent: Box[Key] = Empty

  /**
   * Save the contents of the record to the data store.
   */
  def save(): Boolean = meta.put(this).isDefined
}
