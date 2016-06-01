package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import utils.ExpectedFormat._
import controllers.session.InsufficientRightsException
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database.TicketDBRepository 
import play.api.libs.json.Json
import scala.util.{Failure, Success, Try}
import scala.concurrent.Future

@Singleton
class TicketController @Inject() extends CRMController {
  import utils.JSFormat.ticketFrmt

  //TODO: add permissions check
  def newTicket(companyId: Int) = CRMActionAsync[Ticket](expectedTicketFormat) { rq => 
    // if(rq.header.belongsToCompany(companyId)){
       TicketDBRepository.createTicket(rq.body, companyId).map( ticket => Json.toJson(ticket))
    // }else{ Future{Failure(new InsufficientRightsException())} }
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
 
}
