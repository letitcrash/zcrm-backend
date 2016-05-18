package utils.converters

import database.tables.DelegateEntity
import models.Delegate

object DelegateConverter {

  implicit class EntityToDelegate(d: DelegateEntity) {
    def asDelegate: Delegate = Delegate(d.id, d.name)
  }

  implicit class DelegateToEntity(d: Delegate) {
    def asDelegateEntity(companyId: Int): DelegateEntity = {
      DelegateEntity(d.id, companyId, d.name)
    }
  }

}
