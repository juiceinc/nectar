package com.juiceanalytics.nectar {
package snippet {

import _root_.net.liftweb.util._
import Helpers._

class HelloWorld {
  def howdy: CssBind = {
    "#time *" #> (new _root_.java.util.Date).toString
  }
}

}

}
