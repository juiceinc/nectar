package appengine

import _root_.net.liftweb.common._
import _root_.net.liftweb.record.field._
import com.google.appengine.api.datastore.Entity


/**
 * Holds the synthetics entity property names used to get/put the hashed password
 * and salt values.
 */
object AppEnginePasswordField {
  private val ENTITY_PROP_HASHED_PWD = "pwdfld__hpw"
  private val ENTITY_PROP_SALT       = "pwdfld__slt"
}


/**
 * Is a field for storing a password in a Google App Engine's datastore entity. Both
 * the hashed password and the salt value are store.
 *
 * <p>This class handles two cases missing in PasswordField: 1) the original salt value is stored for
 * subsequent password match tests, and 2) the hashed password is not encrypted multiple times.</p>
 *
 * @author Jon Buffington
 */
class AppEnginePasswordField[OwnerType <: AppEngineRecord[OwnerType]](rec: OwnerType)
    extends PasswordField(rec) with Loggable {

  private var isExtracting: Boolean = false

  protected def whileExtracting(f: => Box[String]): Box[String] = {
    isExtracting = true
    val r = f
    isExtracting = false
    r
  }

  /**
   * Get the archived password from an entity.
   */
  def getFromEntity(entity: Entity): Box[String] = {
    val p = entity.getProperty(AppEnginePasswordField.ENTITY_PROP_HASHED_PWD)
    val s = entity.getProperty(AppEnginePasswordField.ENTITY_PROP_SALT)
    if (p == null || s == null) {
      logger.warn("Attempted to read missing password data from entity.")
      setBox(Empty)
    }
    else {
      whileExtracting {
        salt.set(s.toString)
        setBox(Full(p.toString))
      }
    }
  }

  /**
   * Put the archived password into an entity.
   */
  def putToEntity(entity: Entity): Unit = {
    entity.setUnindexedProperty(AppEnginePasswordField.ENTITY_PROP_HASHED_PWD, value)
    entity.setUnindexedProperty(AppEnginePasswordField.ENTITY_PROP_SALT, salt.get)
  }

  /**
   * Does not re-hash a hashed password.
   */
  override def set_!(in: Box[String]): Box[String] = {
    val hashedIn = super.set_!(in)
    if (isExtracting) {
      in
    }
    else {
      hashedIn
    }
  }

}
