package utils.converters

import java.text.DecimalFormat

import database.tables.TicketEntity
import models.Ticket

object TicketConverter {
  
  implicit class EntityToTicket (t: TicketEntity) {
      def asTicket: Ticket= {
              Ticket(id = Some(new DecimalFormat("#000000").format(t.id.get)),
                     companyId = t.companyId,
                     createdByUserId = t.createdByUserId,
                     requestedByUserId = t.requestedByUserId,
                     assignedToUserId = t.assignedToUserId,
                     assignedToTeamId = t.assignedToTeamId match { case Some(x) => t.assignedToTeamId; case _ => None},
                     commentId = t.commentId match { case Some(x) => t.commentId; case _ => None} ,
                     status = t.status,
                     subject = t.subject,
                     description = t.description match { case Some(x) => t.description; case _ => None})    
      }
  }

  implicit class TicketToEntity(t: Ticket){
      def asTicketEntity(companyId: Int): TicketEntity = {
              TicketEntity(id = t.id match { case Some(x) => Some(Integer.parseInt(t.id.get)); case _ => None},
                           companyId = t.companyId,
                           createdByUserId = t.createdByUserId,
                           requestedByUserId = t.requestedByUserId,
                           assignedToUserId = t.assignedToUserId,
                           assignedToTeamId = t.assignedToTeamId match { case Some(x) => t.assignedToTeamId; case _ => None},
                           commentId = t.commentId match { case Some(x) => t.commentId; case _ => None} ,
                           status = t.status,
                           subject = t.subject,
                           description = t.description match { case Some(x) => t.description; case _ => None})      
      }
  }
}


