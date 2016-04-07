package utils.converters

import scala.language.postfixOps
import ContactProfileConverter.EntityToProfile
import database.tables.{CompanyEntity, ContactProfileEntity}
import models.Company


object CompanyConverter {
  implicit class EntitiesToCompany
  [COMPANYENTITY <: CompanyEntity, CONTACTENTITY <: ContactProfileEntity]
  (tuple: (COMPANYENTITY, CONTACTENTITY)) {
    def asCompany = {
      Company(
        id = tuple._1.id,
        name = tuple._1.name,
        contactProfile = Some(tuple._2 asProfile),
        vatId = tuple._1.vatId,
        lastModified = tuple._1.lastModified
      )
    }
  }

  implicit class CompanyToEntity[COMPANYENTITY <: Company](company: COMPANYENTITY) {
    def asEntity(profileId: Int): CompanyEntity = {
      CompanyEntity(
        id = company.id,
        name = company.name,
        contactProfileId = profileId,
        vatId = company.vatId,
        lastModified = company.lastModified)
    }
  }

}
