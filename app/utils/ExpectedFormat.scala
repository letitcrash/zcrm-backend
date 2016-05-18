package utils

import play.api.libs.json.Json
import scala.collection.immutable.ListMap

object ExpectedFormat {

  val expectedContactProfileFormat = Json.toJson(ListMap(
    "id"                -> "[O] (int) id of the profile",
    "firstname"         -> "[O] (string)",
    "lastname"          -> "[O] (string)",
    "email"             -> "[O] (string)",
    "address"           -> "[O] (string)",
    "city"              -> "[O] (string)",
    "zipCode"           -> "[O] (string)",
    "phoneNumberMobile" -> "[O] (string)",
    "phoneNumberHome"   -> "[O] (string)",
    "phoneNumberWork"   -> "[O] (string)"
  ))

  val expectedInviteEmployeeFormat = Json.toJson(ListMap(
      "username"                -> Json.toJson("[M] (string) The desired username, normally email"),
      "baseUrl"                 -> Json.toJson("[M] (string) The baseUrl onto which to append the token and user id"),
      "contactProfile"          -> expectedContactProfileFormat,
      "employeeLevel"           -> Json.toJson("[O] (int) The user level, defaults to employee (9999)"),
      "employeeType"            -> Json.toJson("[O] (string) The type of this employee, if not set, no type is set")
  ))

  val expectedEmployeeFormat = Json.toJson(ListMap(
      "id"                      -> Json.toJson("[O] (int) Employee Id"),
      "user"                    -> expectedUserFormat,
      "companyId"               -> Json.toJson("[M] (int) Company Id for user"),
      "employeeType"            -> Json.toJson("[O] (string) "),
      "employeeLevel"           -> Json.toJson("[M] (int) Level of employee")
    ))

  val expectedLoginRqFormat = Json.toJson(ListMap(
      "username" -> "[M] (string) Username",
      "password" -> "[M] (string) Password"
  ))

  val expectedSendEmailRqFormat = Json.toJson(ListMap(
    "email" -> "[M] (string) The email to send the signup link to",
    "url"   -> "[M] (string) The url to use in the link"))


  val expectedActivateUserRqFormat = Json.toJson(ListMap(
    "token"           -> Json.toJson("[M] (string) The token used received in the activation email"),
    "password"        -> Json.toJson("[M] (string) The desired password"),
    "email"           -> Json.toJson("[M] (string) The email used to register"),
    "companyName"     -> Json.toJson("[M] (string) The desired company name"),
    "vatId"           -> Json.toJson("[M] (string) A valid vatId(organisationsnmr in sweden)"),
    "contactProfile"  -> Json.toJson(ListMap(
        "firstname"         -> "[O] (string) Person firstname",
        "lastname"          -> "[O] (string) Person lastname",
        "address"           -> "[O] (string) address",
        "city"              -> "[O] (string) city",
        "zipCode"           -> "[O] (string) numeric zipCode",
        "phoneNumberHome"   -> "[O] (string) numeric, space and '-'",
        "phoneNumberMobile" -> "[O] (string) numeric, space and '-'",
        "phoneNumberWork"   -> "[O] (string) numeric, space and '-'"))))



  val expectedUserFormat = Json.toJson(ListMap(
    "id" -> Json.toJson("[M](int) user id"),
    "username" -> Json.toJson("[M](string) username"),
    "userLevel" -> Json.toJson("[M](int) user level"),
    "contactProfile" -> expectedContactProfileFormat
  ))

  val expectedSetPasswordUsingToken = Json.toJson(
    ListMap("password" -> "[M](string) The password to set",
            "token" -> "[M](string) The token used to validate")
  )

  val expectedMailToSendFormat = Json.toJson(ListMap(
      "subject" -> "[O] (string) subject ",
      "body" -> "[O] (string) body: HTML",
      "to" -> "[M] (List[string]) TO:<email>"
  ))

  val expectedExtMailIdFormat = Json.toJson(ListMap(
    "id" -> "[M](string) Exchange unique Id"
  ))

  val expectedTeamFormat = Json.toJson(ListMap(
    "companyId"      -> Json.toJson("[M](int) company ID"),
    "name"           -> Json.toJson("[M](string) Team name"),
    "description"    -> Json.toJson("[O](string) Team description")
  ))

  val expectedUnionFormat = Json.toJson(ListMap(
    "companyId"      -> Json.toJson("[M](int) company ID"),
    "name"           -> Json.toJson("[M](string) Team name"),
    "description"    -> Json.toJson("[O](string) Team description")
  ))

  val expectedShiftFormat = Json.toJson(ListMap(
    "companyId"      -> Json.toJson("[M](int) company ID"),
    "name"           -> Json.toJson("[M](string) Team name")
  ))

  val expectedTaskFormat =  Json.toJson(ListMap(
    "companyId"      -> Json.toJson("[M](int) company ID"),
    "createdByUser"  ->  expectedUserFormat,
    "assignedToUser" ->  expectedUserFormat,
    "title"          -> Json.toJson("[M](string) Task title"),
    "description"    -> Json.toJson("[O](string) Task description"),
    "status"         -> Json.toJson("[O](string) One of the task statuses: NEW|OPEN|POSTPONED|RESOLVED"),
    "attachedMails"  -> Json.toJson(List(ListMap(
        "Id"         -> "[M](string) Exchange mail ID",
        "subject"    -> "[M](string) Mail subject",
        "fromEmail"  -> "[M](string) Mail sender email"))),
    "dueDate"        -> Json.toJson("[O](string) Task to due date") 
  ))
  
  val expectedInboxMailFormat = Json.toJson(ListMap(
	"id"           -> Json.toJson("[M](string) Exchenge unique ID"),
	"subject"      -> Json.toJson("[M](string) Mail subject"),
	"fromEmail"    -> Json.toJson("[M](string) Sender Email"),
	"fromName"		 -> Json.toJson("[O](string) Sender name"),
	"toEmail"			 -> Json.toJson("[O](string) Recipient email"),
	"toName"			 -> Json.toJson("[O](string) Recipient name"),
	"body"				 -> Json.toJson("[O](string) Mail body"),
	"isRead"			 -> Json.toJson("[O](boolean) Is mail read")
  ))
  
  val expectedMailboxFormat = Json.toJson(ListMap(
	"userId"				 -> Json.toJson("[M](int) User id"),
	"server"				 -> Json.toJson("[M](string) Exchange server address(full)"),
	"login"					 -> Json.toJson("[M](string) Exchange mailbox login formatted like username@domain"),
	"password"			 -> Json.toJson("[M](string) Exchange mailbox password")
  ))

  val expectedPositionFormat = Json.toJson(ListMap(
	"id"											-> Json.toJson("[O] (int) Position Id"),
	"companyId"								-> Json.toJson("[M] (int) Company Id"),
	"name"						        -> Json.toJson("[M] (string) name ")
  ))

  val expectedDelegateFormat = Json.toJson(ListMap(
    "companyId" -> Json.toJson("[M] (int) Company Id"),
    "name"      -> Json.toJson("[M] (string) Delegate name")
  ))
}

