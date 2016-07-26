package utils.converters

import database.tables.{TicketActionEntity, TicketActionAttachedMailEntity, TicketActionAttachedFileEntity, UserEntity, ContactProfileEntity, FileEntity, ExchangeSavedMailEntity}
import models.{TicketAction, TicketActionAttachedMail,TicketActionAttachedFile}

object TicketActionConverter {
  
  implicit class EntityToAction (t: (TicketActionEntity, (UserEntity, ContactProfileEntity))) {
      import utils.converters.UserConverter._
      import utils.converters.MailConverter._
      import utils.converters.FileConverter._
      def asAction: TicketAction= {
        TicketAction(id = t._1.id,
                     parentActionId = t._1.parentActionId,
                     ticketId = t._1.ticketId,
                     user = t._2.asUser,
                     actionType = t._1.actionType,
                     comment = t._1.comment,
                     createdAt = Some(t._1.createdAt))
      }

      def asAction(file: Option[FileEntity] = None, mail: Option[ExchangeSavedMailEntity] = None): TicketAction= {
        TicketAction(id = t._1.id,
                     parentActionId = t._1.parentActionId,
                     ticketId = t._1.ticketId,
                     user = t._2.asUser,
                     actionType = t._1.actionType,
                     comment = t._1.comment,
                     mail = mail.map(_.asMailFromSaved),
                     file = file.map(_.asUploadedFile),
                     createdAt = Some(t._1.createdAt))
      }
  }

  implicit class ActionToEntity(a: TicketAction){
      def asActionEntity(): TicketActionEntity = {
              TicketActionEntity(id = a.id,
                                 parentActionId = a.parentActionId,
                                 ticketId = a.ticketId,
                                 userId = a.user.id.get,
                                 actionType = a.actionType,
                                 comment = a.comment)
      }
  }

  implicit class AttachedMailEntityToAttachedMail (a: TicketActionAttachedMailEntity) {
      def asAttachedMailAction: TicketActionAttachedMail= {
        TicketActionAttachedMail(actionId = a.actionId,
                                 mailId = a.mailId)
      }
  }

  implicit class AttachedMailToEntity(a: TicketActionAttachedMail){
      def asAttachedActionMailEntity: TicketActionAttachedMailEntity = {
        TicketActionAttachedMailEntity(actionId = a.actionId,
                                       mailId = a.mailId)
      }
  }

  implicit class AttachedFileEntityToAttachedFile (f: TicketActionAttachedFileEntity) {
      def asAttachedFileAction: TicketActionAttachedFile= {
        TicketActionAttachedFile(actionId = f.actionId,
                                 fileId = f.fileId)
      }
  }

  implicit class AttachedFileToEntity(f: TicketActionAttachedFile){
      def asAttachedActionFileEntity: TicketActionAttachedFileEntity = {
        TicketActionAttachedFileEntity(actionId = f.actionId,
                                       fileId = f.fileId)
      }
  }
}


