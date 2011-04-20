package appengine

import com.googlecode.objectify._
import java.util.{ArrayList, List}

/**
 * Is the base class for all GAE Data Access Objects (DAO).
 * This class uses the
 * <a href="http://code.google.com/p/objectify-appengine/">Objectify-AppEngine</a>
 * library as convenience layer above the GAE/J datastore API.
 *
 * @author Jon Buffington
 */
abstract class DAO[T <: AnyRef] extends helper.DAOBase {
  /**
   * Is an implicit conversion from a Manifest to a Class.
   */
  protected implicit def manifestToClass(manifest: Manifest[T]): Class[T] = manifest.erasure.asInstanceOf[Class[T]]

  /**
   * Surrounds the call-by-name parameter f with try and return None in the catch expression.
   *
   * @param f Is the call-by-name block to evaluate.
   * @return Returns either Some(evaluation result) or None if f throws an exception.
   */
  protected def trySomeOrNone[T](f: => T): Option[T] = {
    try {
      Some(f)
    }
    catch {
      case _ => None
    }
  }

  /**
   * Registers the class that is mapped to a GAE entity.
   * All entity classes must be registered with the underlying ObjectifyService.
   *
   * <p>To implement, just add the following to your derived DAO class:</p>
   * <p><code>register()</p>
   */
  protected def register()(implicit manifest: Manifest[T]) {
    ObjectifyService.register(manifest)
  }

  /**
   * Save the entity to the datastore.
   *
   * <p>If your entity has a null Long id, an id will be auto-generated and a new entity
   * will be created in the database. If your entity already has an id
   * (either id or name) value, any existing entity in the datastore with
   * that id/name will be overwritten.</p>
   *
   * @param entity Is the entity to save.
   * @return Returns the saved entity Key.
   */
  def save(entity: T): Key[T] = ofy.put(entity)

  /**
   * Delete an entity the datastore.
   *
   * @param entity Is the entity to delete using its @Id field.
   */
  def delete(entity: T) {
    ofy.delete(entity)
  }

  /**
   * Find an entity using its entity ID.
   *
   * @param id Is the entity ID to locate.
   * @return Returns either Some(entity) or None.
   */
  def findById(id: Long)(implicit manifest: Manifest[T]): Option[T] = {
    trySomeOrNone(ofy.get(manifest, id))
  }

  /**
   * Find an entity using its entity name.
   *
   * @param name Is the entity name to locate.
   * @return Returns either Some(entity) or None.
   */
  def findByName(name: String)(implicit manifest: Manifest[T]): Option[T] = {
    trySomeOrNone(ofy.get(manifest, name))
  }



}
