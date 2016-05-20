package utils.converters

import database.tables.ActionEntity
import models.Action

object ActionConverter {
  
  implicit class EntityToAction (a: ActionEntity) {
      def asAction: Action= {
              Action(id = a.id,
                parentActionId = a.parentActionId,
                ticketId = a.ticketId,
                userId = a.userId,
                actionType = a.actionType,
                name = a.name,
                comment = a.comment)
      }
  }

  implicit class ActionToEntity(a: Action){
      def asActionEntity(companyId: Int): ActionEntity = {
              ActionEntity(id = a.id,
                parentActionId = a.parentActionId,
                ticketId = a.ticketId,
                userId = a.userId,
                actionType = a.actionType,
                name = a.name,
                comment = a.comment)
      }
  }
}


