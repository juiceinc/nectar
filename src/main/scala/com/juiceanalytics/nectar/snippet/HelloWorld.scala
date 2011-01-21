package com.juiceanalytics.nectar {
package snippet {

import _root_.scala.xml.{NodeSeq, Text}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import Helpers._

class HelloWorld extends Loggable {
  def howdy: CssBind = {
    // TODO Remove the logging line below as it is only a testing for java.util.logging set-up.
    logger.info("HelloWorld does howdy.")

    "#time *" #> (new _root_.java.util.Date).toString
  }
}

}

}
