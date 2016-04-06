package models

case class Employee(
                     id: Option[Int] = None,
                     user: Option[User] = None,
                     companyId: Int,
                     employeeType: Option[String] = None,
                     employeeLevel: Int = UserLevels.USER,
                     isSubContractor: Boolean = false,
                     subContractorCompanyName: Option[String] = None,
                     subContractorOrgNr: Option[String] = None,
                      employmentNumberType: Option[String] = None,
                     employmentNumberValue: Option[String] = None,
                     comment: Option[String])