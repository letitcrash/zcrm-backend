package utils

import play.api.libs.json.Json

object ExpectedFormat {

  val contactProfile = Json.toJson(Map(
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
      "contactProfile"          -> contactProfile,
      "employeeLevel"           -> Json.toJson("[O](int) The user level, defaults to employee (9999)"),
      "employeeType"            -> Json.toJson("[O](string) The type of this employee, if not set, no type is set")
    ))

}

