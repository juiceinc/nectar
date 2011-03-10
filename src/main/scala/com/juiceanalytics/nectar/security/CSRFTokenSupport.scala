package com.juiceanalytics.nectar.security

import java.security.SecureRandom
import org.scalatra.ScalatraKernel


/*
 * The following file was lifted from the HEAD of scalatra as of 23-Feb-11.
 * TODO Remove this file when scalatra 2.0 is releases.
 */
object GenerateId {
  private lazy val rng = new SecureRandom

  def apply(): String = {
    generateCSRFToken
  }

  private def hexEncode(bytes: Array[Byte]) = ((new StringBuilder(bytes.length * 2) /: bytes) {
    (sb, b) =>
      if ((b.toInt & 0xff) < 0x10) {
        sb.append("0")
      }
      sb.append(Integer.toString(b.toInt & 0xff, 16))
  }).toString

  protected def generateCSRFToken = {
    val tokenVal = new Array[Byte](20)
    rng.nextBytes(tokenVal)
    hexEncode(tokenVal)
  }
}

trait CSRFTokenSupport {
  this: ScalatraKernel =>

  protected def csrfKey: String = ScalatraKernel.csrfKey

  protected def csrfToken: String = session(csrfKey).toString

  before {
    if (isForged) {
      halt(403, "Request tampering detected.")
    }
    prepareCSRFToken
  }

  /**
   * Is a list of URI paths to exclude form testing for request forgery.
   */
  protected def exclusions: List[String] = List()

  /**
   * Tests whether a POST request is a potential cross-site forgery.
   */
  protected def isForged: Boolean = {
    //    request.isWrite && session.get(csrfKey) != params.get(csrfKey)
    if (request.isWrite) {
      if (exclusions.forall(requestPath.startsWith(_))) {
        return false
      }
      else {
        session.get(csrfKey) != params.get(csrfKey)
      }
    }
    else {
      false
    }
  }

  protected def prepareCSRFToken = {
    session.getOrElseUpdate(csrfKey, GenerateId())
  }
}