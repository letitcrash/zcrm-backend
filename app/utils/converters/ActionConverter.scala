package utils.converters

import database.tables.TicketActionEntity
import models.TicketAction

object ActionConverter {
  
  implicit class EntityToAction (a: TicketActionEntity) {
      def asAction: TicketAction= {
        TicketAction(id = a.id,
                parentActionId = a.parentActionId,
                ticketId = a.ticketId,
                userId = a.userId,
                actionType = a.actionType,
                comment = a.comment)
      }
  }

  implicit class ActionToEntity(a: TicketAction){
      def asActionEntity(companyId: Int): TicketActionEntity = {
              TicketActionEntity(id = a.id,
                parentActionId = a.parentActionId,
                ticketId = a.ticketId,
                userId = a.userId,
                actionType = a.actionType,
                comment = a.comment)
      }
  }
}


