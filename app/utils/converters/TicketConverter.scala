package utils.converters

import java.text.DecimalFormat

import database.tables.{TicketEntity, CompanyEntity, UserEntity, ContactProfileEntity, TeamEntity, TicketMemberEntity, TicketTeamMemberEntity}
import models.{Ticket, AggregatedTicket}

object TicketConverter {
  
  implicit class AggEntityToTicket (t: (TicketEntity, (UserEntity, ContactProfileEntity))) {
      import utils.converters.UserConverter._
      def asTicket(members: List[(TicketMemberEntity, (UserEntity, ContactProfileEntity))]): Ticket= {
              Ticket(id = Some(new DecimalFormat("#000000").format(t._1.id.get)),
                     projectId = t._1.projectId,
                     createdByUserId = t._1.createdByUserId,
                     createdByUser = Some(t._2.asUser),
                    //requestedByUserId = t.requestedByUserId match { case Some(x) => t.requestedByUserId; case _ => None },
                    //assignedToUserId = t.assignedToUserId match { case Some(x) => t.assignedToUserId; case _ => None},
                    //assignedToTeamId = t.assignedToTeamId match { case Some(x) => t.assignedToTeamId; case _ => None},
                     members = Some(members.map( m => m._2.asUser)),
                     status = t._1.status,
                     priority = t._1.priority,
                     subject = t._1.subject,
                     description = t._1.description match { case Some(x) => t._1.description; case _ => None})    
      }
  }

  implicit class EntityToTicket (t: TicketEntity) {
      def asTicket: Ticket= {
              Ticket(id = Some(new DecimalFormat("#000000").format(t.id.get)),
                     projectId = t.projectId,
                     createdByUserId = t.createdByUserId,
                    //requestedByUserId = t.requestedByUserId match { case Some(x) => t.requestedByUserId; case _ => None },
                    //assignedToUserId = t.assignedToUserId match { case Some(x) => t.assignedToUserId; case _ => None},
                    //assignedToTeamId = t.assignedToTeamId match { case Some(x) => t.assignedToTeamId; case _ => None},
                     status = t.status,
                     priority = t.priority,
                     subject = t.subject,
                     description = t.description match { case Some(x) => t.description; case _ => None})    
      }
  }

  implicit class TicketToEntity(t: Ticket){
      def asTicketEntity(companyId: Int): TicketEntity = {
              TicketEntity(id = t.id match { case Some(x) => Some(Integer.parseInt(t.id.get)); case _ => None},
                           projectId = t.projectId,
                           createdByUserId = t.createdByUserId,
                           //requestedByUserId = t.requestedByUserId match { case Some(x) => t.requestedByUserId; case _ => None },
                           //assignedToUserId  = t.assignedToUserId match { case Some(x) => t.assignedToUserId; case _ => None},
                           //assignedToTeamId  = t.assignedToTeamId match { case Some(x) => t.assignedToTeamId; case _ => None},
                           status = t.status,
                           priority = t.priority,
                           subject = t.subject,
                           description = t.description match { case Some(x) => t.description; case _ => None})      
      }
  }

  implicit class AggregatedTicketEnttToTicket
  (tup: (((((TicketEntity, (CompanyEntity, ContactProfileEntity)), Option[(UserEntity, ContactProfileEntity)]), 
                             Option[(UserEntity, ContactProfileEntity)]), Option[(UserEntity, ContactProfileEntity)]), Option[TeamEntity]) ) {
    def asTicket(): AggregatedTicket = {
      import UserConverter._
      import CompanyConverter._
      import TeamConverter._

      val ticketEntt = tup._1._1._1._1._1 
      val companyTup = tup._1._1._1._1._2
      val createdByUserTup = tup._1._1._1._2
      val requestedByUserTup = tup._1._1._2
      val assignetToUserTup = tup._1._2
      val assignedToTeamTup = tup._2
            
      AggregatedTicket(id = Some(new DecimalFormat("#000000").format(ticketEntt.id.get)),
                       company = companyTup.asCompany,
                       createdByUser = createdByUserTup.get.asUser,
                       requestedByUser = requestedByUserTup match { case Some(x) => Some(requestedByUserTup.get.asUser); case _ => None},
                       assignedToUser = assignetToUserTup match { case Some(x) => Some(assignetToUserTup.get.asUser); case _ => None},
                       assignedToTeam = assignedToTeamTup match { case Some(x) => Some(assignedToTeamTup.get.asTeam); case _ => None},
                       status = ticketEntt.status,
                       priority = ticketEntt.priority,
                       subject = ticketEntt.subject,
                       description = ticketEntt.description match { case Some(x) => ticketEntt.description; case _ => None})      
    }
  }

  implicit class AggregatedTicketToTicketEntt(t: AggregatedTicket) {
    def asTicketEntity(): TicketEntity = {
      TicketEntity(id = t.id match { case Some(x) => Some(Integer.parseInt(t.id.get)); case _ => None},
                   projectId = t.company.id,
                   createdByUserId = t.createdByUser.id.get,
                   requestedByUserId = t.requestedByUser match { case Some(x) => x.id; case _ => None },
                   assignedToUserId = t.assignedToUser match { case Some(x) => x.id; case _ => None},
                   assignedToTeamId = t.assignedToTeam match { case Some(x) => x.id; case _ => None},
                   status = t.status,
                   priority = t.priority,
                   subject = t.subject,
                   description = t.description match { case Some(x) => t.description; case _ => None})      
    }
  }


  implicit class TupMemberToEntity(t: (Int, Int)) {
    def asTicketMemberEntt(): TicketMemberEntity = {
      TicketMemberEntity(t._1, t._2)
    }
  }

  implicit class TupTeamMemberToEntity(t: (Int, Int)) {
    def asTicketTeamMemberEntt(): TicketTeamMemberEntity = {
      TicketTeamMemberEntity(t._1, t._2)
    }
  }

}


