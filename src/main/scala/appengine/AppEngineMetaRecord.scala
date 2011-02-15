package appengine

import com.google.appengine.api.datastore._
import net.liftweb.common._
import net.liftweb.record._
import net.liftweb.util.ControlHelpers._


/**
 * Holds the meta information and operations on Google App Engine records.
 *
 * @author Jon Buffington
 */
trait AppEngineMetaRecord[BaseRecord <: AppEngineRecord[BaseRecord]] extends MetaRecord[BaseRecord] with Loggable {
  this: BaseRecord =>

  /**
   * Make a record using the Entity's properties and values. The newly created record will contain
   * the Entity's Key.
   *
   * @param entity Is the entity that supplies the values to populate the record.
   * @returns Returns a newly created record with fields populated from the Entity's properties and values.
   */
  protected def mkRecord(entity: Entity): BaseRecord = fromEntity(entity, createRecord)

  /**
   * Get a record using a datastore Key.
   *
   * @param key Is the datastore Key use to locate a record.
   * @returns Returns either a populated Full(record) or Empty.
   */
  def get(key: Key): Box[BaseRecord] = {
    tryo(datastore.get(key)) match {
      case Full(entity) => Full(mkRecord(entity))
      case Failure(msg, _, _) => {
        logger.warn(msg); Empty
      }
      case Empty => Empty
    }
  }

  /**
   * Get a record using a parent Key and entity id.
   *
   * @param parent Is the parent entity's Key.
   * @param id Is the entity's id value use to locate a record.
   * @returns Returns either a populated Full(record) or Empty.
   */
  def get(parent: Key, id: Long): Box[BaseRecord] = get(KeyFactory.createKey(parent, entityKind, id))

  /**
   * Get a record using a parent Key and entity name.
   *
   * @param parent Is the parent entity's Key.
   * @param name Is the entity's name value use to locate a record.
   * @returns Returns either a populated Full(record) or Empty.
   */
  def get(parent: Key, name: String): Box[BaseRecord] = get(KeyFactory.createKey(parent, entityKind, name))

  /**
   * Get a record using an entity id.
   *
   * @param id Is the entity's id value use to locate a record.
   * @returns Returns either a populated Full(record) or Empty.
   */
  def get(id: Long): Box[BaseRecord] = get(KeyFactory.createKey(entityKind, id))

  /**
   * Get a record using an entity's name.
   *
   * @param name Is the entity's name value use to locate a record.
   * @returns Returns either a populated Full(record) or Empty.
   */
  def get(name: String): Box[BaseRecord] = get(KeyFactory.createKey(entityKind, name))


  /**
   * Populate an record's fields with the values from a datastore Entity.
   *
   * @param entity Is the App Engine Entity providing the record values.
   * @param rec Is the destination record to populate with values from the entity.
   * @returns Returns the mutated record.
   */
  protected def fromEntity(entity: Entity, rec: BaseRecord): BaseRecord = {
    rec.allFields.foreach {
      field: Field[_, BaseRecord] =>
        field match {
          case f: AppEnginePasswordField[_] => f.getFromEntity(entity)
          case f => {
            val v = entity.getProperty(f.name)
            if (v != null) {
              f.setFromAny(v)
            }
          }
        }
        runSafe(() => field.resetDirty)
    }

    // Set the datastore meta-data values.
    rec.key = Full(entity.getKey)
    rec
  }

  /**
   * Store the field's value in the datastore Entity.
   *
   * @param field - Is field providing the value.
   * @param entity - Is the Entity that will be modified to store the value.
   */
  protected def setEntityProperty(field: TypedField[_], entity: Entity): Unit = {
    field match {
      case f if f.optional_? && f.valueBox.isEmpty => logger.debug("Optional and empty field '" + f.name + "' ignored.") // Ignore an optional field
      case f: AppEngineIdField[_] => logger.debug("Id field value was saved already a part of the Key.") // Ignore the duplicate AppEngineIdField value
      case f: AppEnginePasswordField[_] => f.putToEntity(entity)
      case f if f.name == "name" || f.name == "id" => logger.debug("Name and ID fields are not stored in an entity as the both are reserved App Engine properties.")
      case f if f.valueBox.isDefined && f.isInstanceOf[IsIndexed] => entity.setProperty(f.name, f.valueBox.get)
      case f if f.valueBox.isDefined => entity.setUnindexedProperty(f.name, f.valueBox.get)
      case f => logger.debug("Unable to save field value for '" + f.name + "' in an entity.")
    // TODO Handle any remaining cases.
    //      case f: DateTimeTypedField => logger.info("Date and time")
    //      case f: BinaryTypedField => logger.info("Byte string, short | long")
    //      case f: EnumTypedField[_] => logger.info("enum -> integer")
    //      case f: EnumNameTypedField[_] => logger.info("enum -> string")
    }
  }

  /**
   * Store a record in the datastore.
   *
   * @param rec Is the record to store.
   * @returns Returns a Full(Key) if successful or empty if the put fails.
   */
  def put(rec: BaseRecord): Box[Key] = {
    // TODO Handle entity groups.

    val entity = rec.key match {
      case Full(k) => new Entity(k)
      case _ => new Entity(entityKind)
    }

    for (f <- rec.allFields) {
      setEntityProperty(f, entity)
    }

    // Save the datastore key in the Record.
    rec.key = tryo(datastore.put(entity))
    rec.key
  }

  /**
   * Find a single datastore Key that references an Entity containing a property that equals the value parameter.
   *
   * @param propertyName Is the name of the property to match.
   * @param value Is the value to match.
   * @returns Returns a Full(Key) if successful or Empty if the query fails.
   */
  def findOneKey(propertyName: String, value: Any): Box[Key] = {
    import collection.JavaConversions._
    import com.google.appengine.api.datastore.FetchOptions.Builder._

    val q = new Query(entityKind)
            .setKeysOnly
            .addFilter(propertyName, Query.FilterOperator.EQUAL, value)
    val rs = datastore.prepare(q).asList(withLimit(1))
    if (rs.length == 0) {
      None
    }
    else {
      Full(rs(0).getKey)
    }
  }

  /**
   * Find a single record containing a property that equals the value parameter.
   *
   * @param propertyName Is the name of the property to match.
   * @param value Is the value to match.
   * @returns Returns either a populated Full(record) or Empty if the query fails.
   */
  def findOne(propertyName: String, value: Any): Box[BaseRecord] = entityToRecord(keyToEntity(findOneKey(propertyName, value)))

  /**
   * Convert a Key to its corresponding Entity by getting the Entity from the datastore.
   *
   * @param key Is the datastore key used to locate the entity.
   * @returns Returns either a Full(Entity) or Empty if the datastore get fails.
   */
  def keyToEntity(key: Box[Key]): Box[Entity] = key match {
    case Full(k) => tryo(datastore.get(k))
    case _ => Empty
  }

  /**
   * Convert an Entity to a record with field values sourced from the Entity.
   *
   * @param entity Is the datastore Entity used to populate the AppEngineRecord's field values.
   * @returns Returns either a Full(record) or Empty if the conversion fails.
   */
  def entityToRecord(entity: Box[Entity]): Box[BaseRecord] = entity match {
    case Full(e) => Full(mkRecord(e))
    case _ => Empty
  }
}


/**
 * Is used to mark fields for indexing when persisting to the datastore.
 */
trait IsIndexed {
  this: BaseField =>
}
