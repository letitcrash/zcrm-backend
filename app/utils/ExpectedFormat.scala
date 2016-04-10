package utils

import play.api.libs.json.Json

object ExpectedFormat {

  val expectedContactProfileFormat = Json.toJson(Map(
    "id" -> "[O](int) id of the profile",
    "firstname" -> "[O](string)",
    "lastname" -> "[O](string)",
    "email" -> "[O](string)",
    "address" -> "[O](string)",
    "city" -> "[O](string)",
    "zipCode" -> "[O](string)",
    "phoneNumberMobile" -> "[O](string)",
    "phoneNumberHome" -> "[O](string)",
    "phoneNumberWork" -> "[O](string)"
  ))

  val expectedNewEmployeeFormat = Json.toJson(Map(
      "username"                -> Json.toJson("[M](string) The desired username, normally email"),
      "baseUrl"                 -> Json.toJson("[M](string) The baseUrl onto which to append the token and user id"),
      "contactProfile"          -> expectedContactProfileFormat,
      "employeeLevel"           -> Json.toJson("[O](int) The user level, defaults to employee (9999)"),
      "employeeType"            -> Json.toJson("[O](string) The type of this employee, if not set, no type is set")
  ))

  val expectedLoginRqFormat = Json.toJson(Map(
			"username" -> "[M](string) Username",
			"password"   -> "[M](string) Password"
	))

  val expectedSendEmailRqFormat = Json.toJson(Map(
    "email" -> "[M](string) The email to send the signup link to",
    "url"   -> "[M](string) The url to use in the link"))


  val expectedActivateUserRqFormat = Json.toJson(Map(
    "token"           -> Json.toJson("[M] (string) The token used received in the activation email"),
    "password"        -> Json.toJson("[M] (string) The desired password"),
    "email"           -> Json.toJson("[M] (string) The email used to register"),
    "companyName"     -> Json.toJson("[M] (string) The desired company name"),
    "vatId"           -> Json.toJson("[M] (string) A valid vatId(organisationsnmr in sweden)"),
    "contactProfile"  -> Json.toJson(
      Map(
        "firstname"         -> "(string) Person firstname",
        "lastname"          -> "(string) Person lastname",
        "address"           -> "(string)",
        "city"              -> "(string)",
        "zipCode"           -> "(string) numeric zipCode",
        "phoneNumberHome"   -> "(string) numeric, space and '-'",
        "phoneNumberMobile" -> "(string) numeric, space and '-'",
        "phoneNumberWork"   -> "(string) numeric, space and '-'"))))


}

