package utils.converters

import database.tables.TicketEntity
import models.Ticket

object TicketConverter {
  
  implicit class EntityToTicket (t: TicketEntity) {
      def asTicket: Ticket= {
              Ticket(id = t.id,
                     companyId = t.companyId,
                     createdByUserId = t.createdByUserId,
                     requestedByUserId = t.requestedByUserId,
                     assignedToUserId = t.assignedToUserId,
                     assignedToTeamId = t.assignedToTeamId,
                     commentId = t.commentId,
                     ticketId = t.ticketId,
                     status = t.status,
                     subject = t.subject,
                     description = t.description)    
      }
  }

  implicit class TicketToEntity(t: Ticket){
      def asTicketEntity(companyId: Int): TicketEntity = {
              TicketEntity(id = t.id,
                           companyId = t.companyId,
                           createdByUserId = t.createdByUserId,
                           requestedByUserId = t.requestedByUserId,
                           assignedToUserId = t.assignedToUserId,
                           assignedToTeamId = t.assignedToTeamId,
                           commentId = t.commentId,
                           ticketId = t.ticketId,
                           status = t.status,
                           subject = t.subject,
                           description = t.description)      
      }
  }
}


