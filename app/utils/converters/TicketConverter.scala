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
                     requestedByUserId = t.requestedByUserId match { case Some(x) => t.requestedByUserId; case _ => None },
                     assignedToUserId = t.assignedToUserId match { case Some(x) => t.assignedToUserId; case _ => None},
                     assignedToTeamId = t.assignedToTeamId match { case Some(x) => t.assignedToTeamId; case _ => None},
                     status = t.status,
                     priority = t.priority,
                     subject = t.subject,
                     description = t.description match { case Some(x) => t.description; case _ => None})    
      }
  }

  implicit class TicketToEntity(t: Ticket){
      def asTicketEntity(companyId: Int): TicketEntity = {
              TicketEntity(id = t.id match { case Some(x) => Some(Integer.parseInt(t.id.get)); case _ => None},
                           companyId = t.companyId,
                           createdByUserId = t.createdByUserId,
                           requestedByUserId = t.requestedByUserId match { case Some(x) => t.requestedByUserId; case _ => None },
                           assignedToUserId = t.assignedToUserId match { case Some(x) => t.assignedToUserId; case _ => None},
                           assignedToTeamId = t.assignedToTeamId match { case Some(x) => t.assignedToTeamId; case _ => None},
                           status = t.status,
                           priority = t.priority,
                           subject = t.subject,
                           description = t.description match { case Some(x) => t.description; case _ => None})      
      }
  }
}


