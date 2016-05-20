package utils.converters

import database.tables.{DelegateEntity, GroupDelegateEntity}
import models.Delegate

object DelegateConverter {

  implicit class EntityToDelegate(d: DelegateEntity) {
    def asDelegate: Delegate = Delegate(d.id, d.name)
  }

  implicit class DelegateWithUserEntityToDelegate(tup:(GroupDelegateEntity, DelegateEntity)) {
    def asDelegate: Delegate = Delegate(
     id =  tup._2.id,
     name = tup._2.name,
     startDate = tup._1.delegateStartDate,
     endDate = tup._1.delegateEndDate)
  }

  implicit class DelegateToEntity(d: Delegate) {
    def asDelegateEntity(companyId: Int): DelegateEntity = {
      DelegateEntity(d.id, companyId, d.name)
    }
  }

}
