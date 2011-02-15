package appengine

import Mailer._
import _root_.net.liftweb._
import common.{Box, Full}
import http.S
import record.field._
import util._


/**
 * Is the Lift record MetaMegaProtoUser "meta" trait adapted to run on Google App Engine.
 *
 * @author Jon Buffington
 */
trait MetaMegaProtoUser[ModelType <: MegaProtoUser[ModelType]] extends record.MetaMegaProtoUser[ModelType] with AppEngineMetaRecord[ModelType] {
  this: ModelType =>

  /**
   * Given an email address, find the user record.
   */
  protected def findUserByUserName(value: String): Box[TheUserType] = findOne(this.email.name, value)

  /**
   * Given a unique opaque identifier, find the user record.
   */
  protected def findUserByUniqueId(id: String): Box[TheUserType] = findOne(this.uniqueId.name, id)

  /**
   * Given a String representing the entity Id, find the user record.
   */
  protected def userFromStringId(id: String): Box[TheUserType] = get(id.toLong)

  /**
   * Hook the {@link MetaMegaProtoUser} magic into AppEngineMetaRecord.
   */
  override protected implicit def typeToBridge(in: TheUserType): UserBridge = new MyAppEngineUserBridge(in)

  protected class MyAppEngineUserBridge(in: TheUserType) extends MyUserBridge(in) {

    // Route the call to the existing Record method.
    override def userIdAsString: String = in.userIdAsString

    // Save the user record to app engine datastore.
    override def save(): Boolean = in.save()
  }

  /**
   * Send a validation email to the enrolled email address.
   */
  override def sendValidationEmail(user: TheUserType): Unit = {
    // TODO Remove the following def when AppEngineMailerImpl extends MailerImpl.
    def generateValidationEmailBodies(user: TheUserType, resetLink: String): List[MailBodyType] =
      List(xmlToMailBodyType(signupMailBody(user, resetLink)))

    val resetLink = S.hostAndPath+"/" + validateUserPath.mkString("/") +
    "/" + Helpers.urlEncode(user.getUniqueId())

    val x = user
    val email: String = user.getEmail

    val msgXml = signupMailBody(user, resetLink)

    appengine.Mailer.sendMail(From(emailFrom),Subject(signupMailSubject),
                    (To(user.getEmail) ::
                     generateValidationEmailBodies(user, resetLink) :::
                     (bccEmail.toList.map(BCC(_)))) :_* )
  }

  /**
   * Send a password reset email to the user.
   */
  override def sendPasswordReset(email: String): Unit = {
    // TODO Remove tbe following def when AppEngineMailerImpl extends MailerImpl.
    def generateResetEmailBodies(user: TheUserType, resetLink: String): List[MailBodyType] =
      List(xmlToMailBodyType(passwordResetMailBody(user, resetLink)))

    findUserByUserName(email) match {
      case Full(user) if user.validated_? =>
        user.resetUniqueId().save
        val resetLink = S.hostAndPath +
          passwordResetPath.mkString("/", "/", "/") + Helpers.urlEncode(user.getUniqueId())

        val email: String = user.getEmail

        appengine.Mailer.sendMail(From(emailFrom),Subject(passwordResetEmailSubject),
                        (To(user.getEmail) ::
                         generateResetEmailBodies(user, resetLink) :::
                         (bccEmail.toList.map(BCC(_)))) :_*)

        S.notice(S.??("password.reset.email.sent"))
        S.redirectTo(homePage)

      case Full(user) =>
        sendValidationEmail(user)
        S.notice(S.??("account.validation.resent"))
        S.redirectTo(homePage)

      case _ => S.error(userNameNotFoundString)
    }
  }
}


/**
 * Is the Lift record MegaProtoUser trait adapted to run on Google App Engine.
 *
 * @author Jon Buffington
 */
trait MegaProtoUser[T <: MegaProtoUser[T]] extends record.MegaProtoUser[T] with AppEngineRecord[T] {
  this: T =>

  /**
   * Confirm that the email address is unique in the database. This is called
   * during the sign-up workflow and to validate account edits (i.e., changing email
   * address to someone else's).
   */
  protected def valUnique(errorMsg: => String)(email: String): List[FieldError] = {
    val key = meta.findOneKey(this.email.name, email)
    if (key.isEmpty || key.get.getId == this.id.get) {
      Nil
    }
    else {
      List(FieldError(this.email, errorMsg))
    }
  }

  /**
   * Is the primary key field for the User and is index for queries.
   */
  override lazy val id: LongField[T] = new AppEngineIdField(this)

  /**
   * Is the email field for the User and is indexed for queries.
   */
  override lazy val email: EmailField[T] = new MyEmail(this, 48) with IsIndexed

  /**
   * Is the login password customized for App Engine archival and retrieval.
   */
  override lazy val password: PasswordField[T] = new AppEnginePasswordField(this)

  /**
   * Is unique id field for the User and is indexed. This field
   * is used for validation, lost passwords, etc.
   */
  override lazy val uniqueId: UniqueIdField[T] = new MyUniqueId(this, 32) with IsIndexed
}
