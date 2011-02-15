package appengine

import _root_.scala.xml.NodeSeq
import _root_.javax.mail._
import _root_.javax.mail.internet._
import _root_.java.util.Properties
import _root_.net.liftweb.common._
import _root_.net.liftweb.util._


/**
 * Utilities for sending email.
 */
object Mailer extends AppEngineMailerImpl

/**
 * Is an App Engine friendly implementation of Lift's MailImpl. Unfortunately much code was copied
 * as the net.liftweb.util.MailImpl has protected access control. A ticket was opened to remove
 * the protected access modifier from net.liftweb.util.MailImpl.
 * 
 * @see https://www.assembla.com/spaces/liftweb/tickets/899-remove-protected-access-modifier-from-trait-mailerimpl
 *
 * @author Jon Buffington
 */
trait AppEngineMailerImpl extends SimpleInjector with Loggable {

  sealed abstract class MailTypes

  sealed abstract class MailBodyType extends MailTypes

  case class PlusImageHolder(name: String, mimeType: String, bytes: Array[Byte])

  /**
   * Represents a text/plain mail body. The given text will
   * be encoded as UTF-8 when sent.
   */
  case class PlainMailBodyType(text: String) extends MailBodyType

  /**
   * Represents a text/plain mail body that is encoded with the
   * specified charset
   */
  case class PlainPlusBodyType(text: String, charset: String) extends MailBodyType

  case class XHTMLMailBodyType(text: NodeSeq) extends MailBodyType

  case class XHTMLPlusImages(text: NodeSeq, items: PlusImageHolder*) extends MailBodyType

  sealed abstract class RoutingType extends MailTypes

  sealed abstract class AddressType(val adr: String) extends RoutingType

  case class From(address: String) extends AddressType(address)

  case class To(address: String) extends AddressType(address)

  case class CC(address: String) extends AddressType(address)

  case class Subject(subject: String) extends RoutingType

  case class BCC(address: String) extends AddressType(address)

  case class ReplyTo(address: String) extends AddressType(address)

  implicit def stringToMailBodyType(text: String): MailBodyType = PlainMailBodyType(text)

  implicit def xmlToMailBodyType(html: NodeSeq): MailBodyType = XHTMLMailBodyType(html)

  case class MessageInfo(from: From, subject: Subject, info: List[MailTypes])

  implicit def addressToAddress(in: AddressType): Address = new InternetAddress(in.adr)

  implicit def adListToAdArray(in: List[AddressType]): Array[Address] = in.map(a => new InternetAddress(a.adr)).toArray


  lazy val properties: Properties = {
    val p = System.getProperties.clone.asInstanceOf[Properties]
    // allow the properties file to set/override system properties
    Props.props.foreach {
      case (name, value) =>
        p.setProperty(name, value)
    }
    p
  }

  /**
   * Set the mail.charset property to something other than UTF-8 for non-UTF-8
   * mail.
   */
  lazy val charSet = properties.getProperty("mail.charset") match {
    case null => "UTF-8"
    case x => x
  }

  protected def performTransportSend(msg: MimeMessage) = {
    import Props.RunModes._
    (Props.mode match {
      case Development => devModeSend.vend
      case Test => testModeSend.vend
      case Staging => stagingModeSend.vend
      case Production => productionModeSend.vend
      case Pilot => pilotModeSend.vend
      case Profile => profileModeSend.vend
    }).apply(msg)
  }

  /**
   * How to send a message in dev mode.  By default, use Transport.send(msg)
   */
  lazy val devModeSend: Inject[MimeMessage => Unit] = new Inject[MimeMessage => Unit]((m: MimeMessage) => Transport.send(m)) {}

  /**
   * How to send a message in test mode.  By default, log the message
   */
  lazy val testModeSend: Inject[MimeMessage => Unit] = new Inject[MimeMessage => Unit]((m: MimeMessage) => logger.info("Sending Mime Message: " + m)) {}

  /**
   * How to send a message in staging mode.  By default, use Transport.send(msg)
   */
  lazy val stagingModeSend: Inject[MimeMessage => Unit] = new Inject[MimeMessage => Unit]((m: MimeMessage) => Transport.send(m)) {}

  /**
   * How to send a message in production mode.  By default, use Transport.send(msg)
   */
  lazy val productionModeSend: Inject[MimeMessage => Unit] = new Inject[MimeMessage => Unit]((m: MimeMessage) => Transport.send(m)) {}

  /**
   * How to send a message in pilot mode.  By default, use Transport.send(msg)
   */
  lazy val pilotModeSend: Inject[MimeMessage => Unit] = new Inject[MimeMessage => Unit]((m: MimeMessage) => Transport.send(m)) {}

  /**
   * How to send a message in profile mode.  By default, use Transport.send(msg)
   */
  lazy val profileModeSend: Inject[MimeMessage => Unit] = new Inject[MimeMessage => Unit]((m: MimeMessage) => Transport.send(m)) {}

  /**
   * Asynchronously send an email.
   */
  def sendMail(from: From, subject: Subject, rest: MailTypes*) {
    try {
      val info = rest.toList
      val session = Session.getDefaultInstance(properties, null)
      val message = new MimeMessage(session)
      message.setFrom(from)
      message.setRecipients(Message.RecipientType.TO, info.flatMap {
        case x: To => Some[To](x) case _ => None
      })
      message.setRecipients(Message.RecipientType.CC, info.flatMap {
        case x: CC => Some[CC](x) case _ => None
      })
      message.setRecipients(Message.RecipientType.BCC, info.flatMap {
        case x: BCC => Some[BCC](x) case _ => None
      })
      // message.setReplyTo(filter[MailTypes, ReplyTo](info, {case x @ ReplyTo(_) => Some(x); case _ => None}))
      message.setReplyTo(info.flatMap {
        case x: ReplyTo => Some[ReplyTo](x) case _ => None
      })
      message.setSubject(subject.subject)
      val bodyTypes = info.flatMap {
        case x: MailBodyType => Some[MailBodyType](x); case _ => None
      }
      bodyTypes match {
        case PlainMailBodyType(txt) :: Nil =>
          message.setText(txt)

        case _ =>
          val multiPart = new MimeMultipart("alternative")
          bodyTypes.foreach {
            tab =>
              val bp = new MimeBodyPart
              tab match {
                case PlainMailBodyType(txt) => bp.setText(txt, "UTF-8")
                case PlainPlusBodyType(txt, charset) => bp.setText(txt, charset)
                case XHTMLMailBodyType(html) => bp.setContent(html.toString, "text/html; charset=" + charSet)
                case XHTMLPlusImages(html, img@_*) =>
                  val html_mp = new MimeMultipart("related")
                  val bp2 = new MimeBodyPart
                  bp2.setContent(html.toString, "text/html; charset=" + charSet)
                  html_mp.addBodyPart(bp2)
                  img.foreach {
                    i =>
                      val rel_bpi = new MimeBodyPart
                      rel_bpi.setFileName(i.name)
                      rel_bpi.setContentID(i.name)
                      rel_bpi.setDisposition("inline")
                      rel_bpi.setDataHandler(new _root_.javax.activation.DataHandler(new _root_.javax.activation.DataSource {
                        def getContentType = i.mimeType

                        def getInputStream = new _root_.java.io.ByteArrayInputStream(i.bytes)

                        def getName = i.name

                        def getOutputStream = throw new _root_.java.io.IOException("Unable to write to item")
                      }))
                      html_mp.addBodyPart(rel_bpi)
                  }
                  bp.setContent(html_mp)
              }
              multiPart.addBodyPart(bp)
          }
          message.setContent(multiPart);
      }

      performTransportSend(message)
    }
    catch {
      case e: Exception => logger.error("Couldn't send mail", e)
    }
  }
}
