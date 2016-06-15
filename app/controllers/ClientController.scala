package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import utils.ExpectedFormat._
import utils.ews.EwsAuthUtil
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database.ClientDBRepository
import play.api.libs.json.Json
import scala.concurrent.Future

@Singleton
class ClientController @Inject() (ewsAuth: EwsAuthUtil) extends CRMController {
  import utils.JSFormat._

  def newClient(companyId: Int) = CRMActionAsync[Client](expectedClientFormat){rq =>
    ClientDBRepository.createClient(rq.body).map( shift => Json.toJson(shift))
  }

  def getClientById(companyId: Int, clientId: Int) = CRMActionAsync{rq =>
    ClientDBRepository.getClientById(clientId).map(res => Json.toJson(res))
  }

  def getAllClientesByCompanyId(companyId: Int) = CRMActionAsync{rq =>
    ClientDBRepository.getClientsByCompanyId(companyId).map(list => Json.toJson(list))
  }

  def updateClient(companyId: Int, clientId: Int) = CRMActionAsync[Client](expectedClientFormat){rq =>
    ClientDBRepository.updateClient(rq.body).map(res => Json.toJson(res))
  }

  def deleteClient(companyId: Int, clientId: Int) = CRMActionAsync{rq =>
    ClientDBRepository.deleteClient(clientId).map(res => Json.toJson(res))
  }
}
