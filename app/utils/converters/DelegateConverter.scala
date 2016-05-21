package utils.converters

import database.tables.{DelegateEntity, GroupDelegateEntity}
import models.{Delegate, DelegateGroup}

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

  implicit class DelegateGroupToEntity(dg: DelegateGroup) {
    def asGroupEntity: GroupDelegateEntity = {
      GroupDelegateEntity(
        delegateId = dg.delegateId,
        userId = dg.userId,
        delegateStartDate = dg.startDate,
        delegateEndDate = dg.endDate)
    }
  }

  implicit class GroupEntityToDelegate(dg: GroupDelegateEntity) {
    def asDelegateGroup: DelegateGroup = {
      DelegateGroup(
        delegateId = dg.delegateId,
        userId = dg.userId,
        startDate = dg.delegateStartDate,
        endDate = dg.delegateEndDate)
    }
  }


  implicit class DelegateToDelegateGroup(dg: Delegate){
    def asDelegateGroup(userId: Option[Int]): DelegateGroup = {
      DelegateGroup(
        delegateId = dg.id,
        userId = userId,
        startDate = dg.startDate,
        endDate = dg.endDate)
    }
  }


}
