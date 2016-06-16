package utils.converters

import java.text.DecimalFormat

import database.tables.{TicketEntity, CompanyEntity, UserEntity, ContactProfileEntity, TeamEntity, TicketMemberEntity, TicketTeamMemberEntity, TicketClientEntity, ClientEntity}
import models.Ticket 

object TicketConverter {
  
  implicit class AggEntityToTicket (t: (TicketEntity, (UserEntity, ContactProfileEntity))) {
      import utils.converters.UserConverter._
      import utils.converters.TeamConverter._
      import utils.converters.ClientConverter._
      def asTicket(members: List[(TicketMemberEntity, (UserEntity, ContactProfileEntity))], teams: List[(TicketTeamMemberEntity , TeamEntity)],
                   clients: List[(TicketClientEntity, (ClientEntity, ContactProfileEntity))]): Ticket= {
              Ticket(id = Some(new DecimalFormat("#000000").format(t._1.id.get)),
                     companyId = t._1.companyId,
                     projectId = t._1.projectId,
                     createdByUserId = t._1.createdByUserId,
                     createdByUser = Some(t._2.asUser),
                    //requestedByUserId = t.requestedByUserId match { case Some(x) => t.requestedByUserId; case _ => None },
                    //assignedToUserId = t.assignedToUserId match { case Some(x) => t.assignedToUserId; case _ => None},
                    //assignedToTeamId = t.assignedToTeamId match { case Some(x) => t.assignedToTeamId; case _ => None},
                     members = Some(members.map( m => m._2.asUser)),
                     teams = Some(teams.map( m => m._2.asTeam)),
                     clients = Some(clients.map( c => c._2.asClient)),
                     status = t._1.status,
                     priority = t._1.priority,
                     subject = t._1.subject,
                     description = t._1.description match { case Some(x) => t._1.description; case _ => None})    
      }
  }

  implicit class EntityToTicket (t: TicketEntity) {
      def asTicket: Ticket= {
              Ticket(id = Some(new DecimalFormat("#000000").format(t.id.get)),
                     companyId = t.companyId,
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
                           companyId = t.companyId,
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

  implicit class TupClientToEntity(t: (Int, Int)) {
    def asTicketClientEntt(): TicketClientEntity = {
      TicketClientEntity(t._1, t._2)
    }
  }

}


