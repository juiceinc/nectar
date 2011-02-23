package appengine

import javax.persistence.Id


trait KeyAsId {
  // A Scala Long is a value an cannot be null.
  // App Engine uses a null Java Long to indicate the entity
  // needs an id generated.
  @Id var id: java.lang.Long = null
}
