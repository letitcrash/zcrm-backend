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
                     assignedToTeamId = t.assignedToTeamId,
                     commentId = t.commentId,
                     status = t.status,
                     subject = t.subject,
                     description = t.description)    
      }
  }

  implicit class TicketToEntity(t: Ticket){
      def asTicketEntity(companyId: Int): TicketEntity = {
              TicketEntity(id = Some(Integer.parseInt(t.id.get)),
                           companyId = t.companyId,
                           createdByUserId = t.createdByUserId,
                           requestedByUserId = t.requestedByUserId,
                           assignedToUserId = t.assignedToUserId,
                           assignedToTeamId = t.assignedToTeamId,
                           commentId = t.commentId,
                           status = t.status,
                           subject = t.subject,
                           description = t.description)      
      }
  }
}


