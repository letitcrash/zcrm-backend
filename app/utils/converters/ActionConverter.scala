package utils.converters

import database.tables.{TicketActionEntity, TicketActionAttachedMailEntity}
import models.{TicketAction, ActionAttachedMail}

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
      def asActionEntity(): TicketActionEntity = {
              TicketActionEntity(id = a.id,
                parentActionId = a.parentActionId,
                ticketId = a.ticketId,
                userId = a.userId,
                actionType = a.actionType,
                comment = a.comment)
      }
  }

  implicit class AttachedMailEntityToAttachedMail (a: TicketActionAttachedMailEntity) {
      def asAttachedMailAction: ActionAttachedMail= {
        ActionAttachedMail(id = a.id,
                           actionId = a.actionId,
                           mailId = a.mailId)
      }
  }

  implicit class AttachedMailToEntity(a: ActionAttachedMail){
      def asAttachedActionEntity: TicketActionAttachedMailEntity = {
        TicketActionAttachedMailEntity(id = a.id,
                                       actionId = a.actionId,
                                       mailId = a.mailId)
      }
  }
}


