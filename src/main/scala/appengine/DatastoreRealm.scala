package appengine

import org.apache.shiro._
import authc._
import authc.credential.HashedCredentialsMatcher
import authz.{SimpleAuthorizationInfo, AuthorizationInfo}
import crypto.hash.{SimpleHash, Sha256Hash}
import crypto.SecureRandomNumberGenerator
import realm.AuthorizingRealm
import subject.PrincipalCollection
import util.SimpleByteSource
import com.juiceanalytics.nectar.model.User


/**
 * The DatastoreRealm provides authentication and authorization of UserEntity instances
 * that are persisted in the Google App Engine Datastore.
 *
 * @author Jon Buffington
 */
class DatastoreRealm extends AuthorizingRealm {
  /**
   * Get the user entity with the given username. If the username is not
   * found, then an UnknownAccountException is thrown.
   *
   * @param username Is the unique account name.
   * @return Returns the user entity that matches the given username.
   */
  protected def findByUsername(username: String): UserEntity = {
    val user = User.findByEmail(username)
    if (user.isEmpty) {
      throw new UnknownAccountException("Unable to find user, '" + username + "' in the datastore.");
    }
    user.get
  }

  override def onInit: Unit = {
    val matcher = new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME)
    matcher.setStoredCredentialsHexEncoded(false) // false means Base64-encoded
    matcher.setHashIterations(UserEntity.hashIterations)
    setCredentialsMatcher(matcher)
  }

  /**
   * Inspects the token for the identifying principal (i.e., account identifying information)
   * and attempts to find the corresponding account data in the datastore.
   * If the credentials match, an AuthenticationInfo instance is returned that encapsulates the account data.
   * If the credentials do not match, an AuthenticationException is thrown.
   *
   * @param token Is the token for the identifying principal.
   * @return Returns an AuthenticationInfo instance or throws an AuthenticationException.
   */
  def doGetAuthenticationInfo(token: AuthenticationToken): AuthenticationInfo = {
    val username = token.asInstanceOf[UsernamePasswordToken].getUsername
    if (username == null) {
      throw new AccountException("A DatastoreRealm does not support null usernames.");
    }
    val user = findByUsername(username)
    new SimpleAuthenticationInfo(user.uniqueName, user.credentials, new SimpleByteSource(user.salt), getName)
  }

  /**
   * Retrieves the AuthorizationInfo for the primary principal from the datastore.
   *
   * @param principals Is the primary identifying principals of the AuthorizationInfo that should be retrieved.
   * @return Returns the AuthorizationInfo associated with the primary principal.
   */
  def doGetAuthorizationInfo(principals: PrincipalCollection): AuthorizationInfo = {
    import collection.JavaConversions._

    val username = principals.getPrimaryPrincipal.toString
    val user = findByUsername(username)
    new SimpleAuthorizationInfo(user.roles)
  }
}

/**
 * The UserEntity trait defines the authentication and authorization requirements for
 * custom user entities stored in the datastore.
 *
 * @author Jon Buffington
 */
trait UserEntity {
  def uniqueName: String

  def credentials: String

  def salt: String

  def roles: Set[String]
}

object UserEntity {
  private lazy val rng = new SecureRandomNumberGenerator

  /**
   * @see http://www.owasp.org/index.php/Hashing_Java#Hardening_against_the_attacker.27s_attack
   */
  val hashIterations = 1024

  /**
   * @retrun Returns a randomly-generated Base64 salt string.
   */
  def salt: String = rng.nextBytes.toBase64

  /**
   * @param plaintext Is the text to encrypt.
   * @param salt Is the salt to use for the hash encryption.
   * @return Returns an encrypted Base64 representation of the plaintext parameter.
   */
  def hashedCredentials(plaintext: String, salt: String): String =
    new SimpleHash(Sha256Hash.ALGORITHM_NAME, plaintext, salt, hashIterations).toBase64
}
