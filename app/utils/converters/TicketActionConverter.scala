package utils.converters

import database.tables.{TicketActionEntity, TicketActionAttachedMailEntity, TicketActionAttachedFileEntity}
import models.{TicketAction, TicketActionAttachedMail,TicketActionAttachedFile}

object TicketActionConverter {
  
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
      def asAttachedMailAction: TicketActionAttachedMail= {
        TicketActionAttachedMail(id = a.id,
                                 actionId = a.actionId,
                                 mailId = a.mailId)
      }
  }

  implicit class AttachedMailToEntity(a: TicketActionAttachedMail){
      def asAttachedActionMailEntity: TicketActionAttachedMailEntity = {
        TicketActionAttachedMailEntity(id = a.id,
                                       actionId = a.actionId,
                                       mailId = a.mailId)
      }
  }

  implicit class AttachedFileEntityToAttachedFile (f: TicketActionAttachedFileEntity) {
      def asAttachedFileAction: TicketActionAttachedFile= {
        TicketActionAttachedFile(id = f.id,
                                 actionId = f.actionId,
                                 fileId = f.fileId)
      }
  }

  implicit class AttachedFileToEntity(f: TicketActionAttachedFile){
      def asAttachedActionFileEntity: TicketActionAttachedFileEntity = {
        TicketActionAttachedFileEntity(id = f.id,
                                       actionId = f.actionId,
                                       fileId = f.fileId)
      }
  }
}


