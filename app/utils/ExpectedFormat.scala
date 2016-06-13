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
    "name"           -> Json.toJson("[M](string) Union name"),
    "description"    -> Json.toJson("[O](string) Union description")
  ))

  val expectedShiftFormat = Json.toJson(ListMap(
    "companyId"      -> Json.toJson("[M](int) company ID"),
    "name"           -> Json.toJson("[M](string) Shift name")
  ))

  val expectedProjectFormat = Json.toJson(ListMap(
    "companyId"      -> Json.toJson("[M](int) company ID"),
    "name"           -> Json.toJson("[M](string) Project name"),
    "description"    -> Json.toJson("[O](string) Description")
  ))

  val expectedDepartmentFormat = Json.toJson(ListMap(
    "companyId"      -> Json.toJson("[M](int) company ID"),
    "name"           -> Json.toJson("[M](string) Department name")
  ))

  val expectedTicketFormat =  Json.toJson(ListMap(
    "id"             -> Json.toJson("[O](int) In DB id"),
    "companyId"      -> Json.toJson("[M](int) Company ID"),
    "createdByUserId"-> Json.toJson("[M](int) Created by userId"),
    "requestedByUserId"-> Json.toJson("[O](int) Requested by userId"),
    "assignedToUserId"-> Json.toJson("[O](int) Assigned to userId"),
    "assignedToTeamId"-> Json.toJson("[O](int) Assigned to team id"),
    "status"          -> Json.toJson("[M](int) Status [1:NEW 2:OPEN 3:POSTPONED 4:RESOLVED]"),
    "priority"        -> Json.toJson("[M](int) Priority [0:LOW 1:MID 2:HIGH]"),
    "subject"         -> Json.toJson("[O](string) Subject"),
    "description"     -> Json.toJson("[O](string) Description")    
  ))

  val expectedAggregatedTicketFormat =  Json.toJson(ListMap(
    "id"              -> Json.toJson("[O](int) In DB id"),
    "company"         -> Json.toJson(ListMap(
          "id"            -> Json.toJson("[M](int) CompanyId"),
          "name"          -> Json.toJson("[O](string) Company Name"),
          "vatId"         -> Json.toJson("[O](string) VatId"),
          "contactProfile"-> expectedContactProfileFormat)),
    "createdByUser"   -> expectedUserFormat,
    "requestedByUser" -> expectedUserFormat,
    "assignedToUser"  -> expectedUserFormat,
    "assignedToTeam"  -> expectedTeamFormat,
    "status"          -> Json.toJson("[M](int) Status [1:NEW 2:OPEN 3:POSTPONED 4:RESOLVED]"),
    "priority"        -> Json.toJson("[M](int) Priority [0:LOW 1:MID 2:HIGH]"),
    "subject"         -> Json.toJson("[O](string) Subject"),
    "description"     -> Json.toJson("[O](string) Description")    
  ))

  def expectedTicketActionFormat = Json.toJson(ListMap(
    "id"              -> Json.toJson("[O](int) ID"),
    "parentActionId"  -> Json.toJson("[O](int) Parent Action ID"),
    "ticketId"        -> Json.toJson("[M](int) Assigned to ticket"),
    "userId"          -> Json.toJson("[M](int) Created by user ID"),
    "actionType"      -> Json.toJson("[M](int) 0 - Comment, 1 - Mail, 2 - File"),
    "comment"         -> Json.toJson("[O](string) Comment")
  ))

  val expectedMailboxFormat = Json.toJson(ListMap(
  "userId"         -> Json.toJson("[M](int) User id"),
  "server"         -> Json.toJson("[M](string) Exchange server address(full)"),
  "login"          -> Json.toJson("[M](string) Exchange mailbox login formatted like username@domain"),
  "password"       -> Json.toJson("[M](string) Exchange mailbox password")
  ))

  val expectedPositionFormat = Json.toJson(ListMap(
  "id"                      -> Json.toJson("[O] (int) Position Id"),
  "companyId"               -> Json.toJson("[M] (int) Company Id"),
  "name"                    -> Json.toJson("[M] (string) name ")
  ))

  val expectedDelegateFormat = Json.toJson(ListMap(
    "companyId" -> Json.toJson("[M] (int) Company Id"),
    "name"      -> Json.toJson("[M] (string) Delegate name")
  ))

  val expectedDelegateGroupFormat = Json.toJson(ListMap(
    "delegateId"    -> Json.toJson("[M] (int) Delegate Id"),
    "userId"    -> Json.toJson("[M] (int) User Id"),
    "startDate" -> Json.toJson("[O] (TS) start date"),
    "endDate"   -> Json.toJson("[O] (TS) end date ")
  ))

}

