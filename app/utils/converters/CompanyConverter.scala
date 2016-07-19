package utils.converters

import scala.language.postfixOps
import ContactProfileConverter.EntityToProfile
import database.tables._
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

    def asAggregatedCompany(//delegateEntts: List[DelegateEntity],
                            shiftEntts: List[ShiftEntity], 
                            departmetEntts: List[DepartmentEntity],
                            unionEntts: List[UnionEntity],
                            teamEntts: List[TeamEntity],
                            positionEntts: List[PositionEntity]) = {
     import DelegateConverter._
     import ShiftConverter._
     import DepartmentConverter._
     import UnionConverter._
     import TeamConverter._
     import PositionConverter._
        Company(
          id = tuple._1.id,
          name = tuple._1.name,
          contactProfile = Some(tuple._2.asProfile),
          vatId = tuple._1.vatId,
         // delegates  = Some(delegateEntts.map(_.asDelegate)),
          shifts = Some(shiftEntts.map(_.asShift)),
          departmets = Some(departmetEntts.map(_.asDepartment)),
          unions = Some(unionEntts.map(_.asUnion)), 
          teams = Some(teamEntts.map(_.asTeam)),
          positions = Some(positionEntts.map(_.asPosition)),
          lastModified = tuple._1.lastModified)
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
