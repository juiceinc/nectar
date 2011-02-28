package appengine

import javax.persistence.Id


trait KeyAsName {
  @Id var name: String = _
}
