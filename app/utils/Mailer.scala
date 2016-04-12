package utils

import play.api.libs.mailer._
import javax.inject.Inject
import play.api._
import scala.util.{Success, Failure, Try}
import play.api.Logger
import models.User
//import java.io.File
//import org.apache.commons.mail.EmailAttachment

class Mailer @Inject() (mailerClient: MailerClient, configuration: Configuration) {

  val signupBody = configuration.getString("mail.signup.body").getOrElse("Link to registration: \n%s")
  val signupTopic = configuration.getString("mail.signup.topic").getOrElse("Welcome to CRM")
  val setPasswordBody = configuration.getString("mail.setPassword.body").getOrElse("Follow this link to set password for you Timeted account:\n%s\n")
  val setPasswordTopic = configuration.getString("mail.setPassword.topic").getOrElse("Invite to Timeted")
  val sender = configuration.getString("mail.sender").getOrElse("info@timeted.se")

  def sendSignupEmail(email: String, token: String, url: String) : Try[Unit]  = {
     val filteredEmail = email.replace("+", "%2b")
     val body = signupBody.format(f"$url?token=$token&email=$filteredEmail")
     sendMail(email, sender, signupTopic, body)
  }

  def sendSetPasswordLink(token: String, baseUrl: String, user: User): Try[Unit] = {
    if(user.contactProfile.isEmpty ||
       user.contactProfile.get.email.isEmpty) {
      return Failure(new Exception("Cannot send set password to empty email"))
    } else if(user.id.isEmpty) {
      return Failure(new Exception("Cannot send mail without user id"))
    }
    val userId = user.id.get
    val email = user.contactProfile.get.email.get
    //TODO: URL should be disscussed with front end devs.
    sendMail(email, sender, setPasswordTopic, setPasswordBody.format(f"$baseUrl/$userId/$token"))
  }

  private def sendMail(to: String, from: String, subject: String, body: String) : Try[Unit] = {
    Logger.info("Sending email to: " + to + ",from: " + from + " ...")
    val email = Email(subject,from,Seq(to),None, Some(body))
    try{
      mailerClient.send(email)
      Success((): Unit)
    }catch {
      case ex: Exception =>
        Failure(ex)
    }
  }

}

