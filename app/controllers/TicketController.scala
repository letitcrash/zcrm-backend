package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import utils.ExpectedFormat._
import controllers.session.InsufficientRightsException
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database.{TicketDBRepository, TicketActionDBRepository,ExchangeODSMailDBRepository,ExchangeSavedMailDBRepository,TicketAttachedMailDBRepository, FileDBRepository, TicketAttachedFileDBRepository}
import play.api.libs.json.Json
import scala.util.{Failure, Success, Try}
import scala.concurrent.Future
import java.text.DecimalFormat

@Singleton
class TicketController @Inject() extends CRMController {
  import utils.JSFormat.ticketFrmt
  import utils.JSFormat.ticketActionFrmt

  //TODO: add permissions check
  def newTicket(companyId: Int) = CRMActionAsync[Ticket](expectedTicketFormat) { rq => 
    // if(rq.header.belongsToCompany(companyId)){
       TicketDBRepository.createTicket(rq.body, companyId).map( ticket => Json.toJson(ticket))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }

  //TODO: add permissions check
  def addCommentToTicket(companyId: Int, ticketId: Int) = CRMActionAsync[TicketAction](expectedTicketActionFormat){ rq =>
    // if(rq.header.belongsToCompany(companyId)){
      TicketActionDBRepository.createAction(rq.body.copy(ticketId = ticketId, actionType = ActionType.COMMENT), companyId).map(ticketAction => Json.toJson(ticketAction))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }

  //TODO: add permissions check
  def attachMailToTicket(companyId: Int, ticketId: Int, mailId: Int) = CRMActionAsync{ rq =>
    // if(rq.header.belongsToCompany(companyId)){
    import utils.JSFormat.exchangeMailFrmt
    for{
          userId       <- ExchangeODSMailDBRepository.getUserIdByODSMailId(mailId)
          odsMail      <- ExchangeODSMailDBRepository.getODSMailById(mailId)
          mail         <- ExchangeSavedMailDBRepository.insertSavedMail(odsMail)
          action       <- TicketActionDBRepository.createAction(TicketAction(ticketId = ticketId, userId = userId, actionType = ActionType.MAIL), companyId)
          attachedMail <- TicketAttachedMailDBRepository.createAttachedMailAction(TicketActionAttachedMail(actionId = action.id.get, mailId = mail.id.get ))
          updatedOds   <- ExchangeODSMailDBRepository.updateODSMail(
                                                      odsMail.copy(subject = Some(odsMail.subject.getOrElse("") + " [Ticket "+(new DecimalFormat("#000000")).format(ticketId)+"]")))
      } yield Json.toJson(mail)    
     
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }

 def detachMailFromTicket(companyId: Int, ticketId: Int, attachedMailId: Int) = CRMActionAsync{rq =>
   import utils.JSFormat.ticketActionMailFrmt
   for{
          deletedAttachedMail <- TicketAttachedMailDBRepository.deleteAttachedMailAction(attachedMailId)
          deletedAction       <- TicketActionDBRepository.deleteAction(deletedAttachedMail.actionId)
   }yield Json.toJson(deletedAttachedMail)
  }

  //TODO: add permissions check
  //TODO: Failure safe
  def attachFileToTicket(companyId: Int, ticketId: Int, fileId: Int) = CRMActionAsync{ rq =>
    // if(rq.header.belongsToCompany(companyId)){
    import utils.JSFormat.ticketActionFileFrmt
    for{
          file         <- FileDBRepository.getFileById(fileId)
          action       <- TicketActionDBRepository.createAction(TicketAction(ticketId = ticketId, userId = file.userId, actionType = ActionType.FILE), companyId)
          attachedFile <- TicketAttachedFileDBRepository.createAttachedFileAction(TicketActionAttachedFile(actionId = action.id.get, fileId = file.id.get ))
      } yield Json.toJson(attachedFile)    
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }

 def detachFileFromTicket(companyId: Int, ticketId: Int, attachedFileId: Int) = CRMActionAsync{rq =>
   import utils.JSFormat.ticketActionFileFrmt
   for{
          deletedAttachedFile <- TicketAttachedFileDBRepository.deleteAttachedFileAction(attachedFileId)
          deletedAction       <- TicketActionDBRepository.deleteAction(deletedAttachedFile.actionId)
   }yield Json.toJson(deletedAttachedFile)
  }


  //TODO: add permissions check
  def updateTicket(companyId: Int, ticketId: Int) = CRMActionAsync[Ticket](expectedTicketFormat){ rq =>
    // if(rq.header.belongsToCompany(companyId)){
      TicketDBRepository.updateTicket(rq.body.copy(id = Some(ticketId.toString)), companyId).map( ticket => Json.toJson(ticket))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }


  //TODO: add permissions check
  def getTicket(companyId: Int, ticketId: Int) = CRMActionAsync { rq =>
    // if(rq.header.belongsToCompany(companyId)){
      TicketDBRepository.getTicketById(ticketId).map( ticket => Json.toJson(ticket))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }

  //TODO: add permissions check
  def getAllTickets(companyId: Int) = CRMActionAsync { rq =>
    // if(rq.header.belongsToCompany(companyId)){
      TicketDBRepository.getTicketsByCompanyId(companyId).map( ticket => Json.toJson(ticket))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }
  
  def deleteTicketById(companyId: Int, ticketId:Int) = CRMActionAsync{rq =>
      TicketDBRepository.deleteTicket(ticketId).map(deletedTicket => Json.toJson(deletedTicket))
  }

  def searchAllTicketsByName(companyId: Int, pageSize: Option[Int], pageNr: Option[Int], searchTerm: Option[String]) = CRMActionAsync{rq =>
    import utils.JSFormat._
    if (pageNr.nonEmpty || pageSize.nonEmpty || searchTerm.nonEmpty) {
      val psize = pageSize.getOrElse(10)
      val pnr = pageNr.getOrElse(1)
      TicketDBRepository.searchTicketByName(companyId, psize, pnr, searchTerm).map(page => Json.toJson(page))
    } else { TicketDBRepository.getTicketsByCompanyId(companyId).map( tickets => Json.toJson(tickets)) }
  }

  def getAllActionWithPagination(companyId: Int, ticketId: Int, actionTypes: List[Int], pageSize: Option[Int], pageNr: Option[Int], searchTerm: Option[String]) = CRMActionAsync{rq =>
    import utils.JSFormat._
    if (pageNr.nonEmpty || pageSize.nonEmpty || searchTerm.nonEmpty) {
      val psize = pageSize.getOrElse(10)
      val pnr = pageNr.getOrElse(1)
      TicketActionDBRepository.getActionWithPagination(ticketId, actionTypes, psize, pnr).map(page => Json.toJson(page))
    } else { TicketActionDBRepository.getActionsByTicketId(ticketId, actionTypes).map( tickets => Json.toJson(tickets)) }
  }

  def getAllAggregatedTicketWithPagination(companyId: Int, createdByUserIds: List[Int], assignedToUserIds: List[Int], assignedToTeamIds: List[Int], pageSize: Option[Int], pageNr: Option[Int]) = CRMActionAsync{rq =>
    import utils.JSFormat._
    TicketDBRepository.getAllAggregatedTickets(companyId, createdByUserIds, assignedToUserIds, assignedToTeamIds, pageSize, pageNr)
                       .map(page => Json.toJson(page))    
  }

  def getAggregadTicket(companyId: Int, ticketId: Int) = CRMActionAsync{rq =>
     import utils.JSFormat._
     TicketDBRepository.getAggregatedTicketById(ticketId).map(aggTicket => Json.toJson(aggTicket))
  }

    import utils.JSFormat._
  def updateAggregatedTicket(companyId: Int, ticketId: Int) = CRMActionAsync[AggregatedTicket](expectedAggregatedTicketFormat){ rq  =>
    TicketDBRepository.updateAggregatedTicket(rq.body).map(updated => Json.toJson(updated))
  } 
 
}
