package appengine

import net.liftweb.record.field.LongField

/**
 * Is a field that sources its value from the record's Key id. This field is intended
 * as the concrete type for Lift's User lazy val id.
 *
 * @author Jon Buffington
 */
class AppEngineIdField[OwnerType <: AppEngineRecord[OwnerType]](rec: OwnerType) extends LongField(rec) with IsIndexed {
  override def value: MyType = if (rec.key.isDefined) {
    rec.key.get.getId
  }
  else {
    defaultValue
  }
}
