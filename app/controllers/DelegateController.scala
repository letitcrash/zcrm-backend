package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import utils.ExpectedFormat._
import database.DelegateDBRepository
import models.{Delegate, DelegateGroup}
import scala.concurrent.Future

class DelegateController @Inject() extends CRMController {
  import utils.JSFormat.delegateFrmt
  import utils.JSFormat.delegateGroupFrmt


  def getDelegate(companyId: Int, delegateId: Int) = CRMActionAsync { rq =>
    DelegateDBRepository.getDelegateById(delegateId).map(Json.toJson(_))
  }

  def newDelegate(companyId: Int) = CRMActionAsync[Delegate](expectedDelegateFormat) { rq =>
    DelegateDBRepository.createDelegate(rq.body, companyId).map(delegate => Json.toJson(delegate))
  }

  def updateDelegate(companyId: Int, delegateId: Int) = CRMActionAsync[Delegate](expectedDelegateFormat) { rq =>
    DelegateDBRepository.updateDelegate(rq.body.copy(id = Some(delegateId)), companyId).map(Json.toJson(_))
  }

  def deleteDelegateById(companyId: Int, delegateId: Int) = CRMActionAsync { rq =>
    DelegateDBRepository.deleteDelegate(delegateId).map(Json.toJson(_))
  }

  def getAllDelegates(companyId: Int) = CRMActionAsync { rq =>
    DelegateDBRepository.getDelegatesByCompanyId(companyId).map(Json.toJson(_))
  }

  def addDelegateToUser(companyId: Int, delegateId: Int, userId: Int) = CRMActionAsync[DelegateGroup](expectedDelegateGroupFormat)  { rq =>
    DelegateDBRepository.addDelegateGroup(rq.body).map(dg => Json.toJson(dg))
  }

  def searchAllDelegatesByName(
      companyId: Int,
      pageSize: Option[Int],
      pageNr: Option[Int],
      searchTerm: Option[String]) = CRMActionAsync { _ =>
    import utils.JSFormat._

    if (pageNr.nonEmpty || pageSize.nonEmpty || searchTerm.nonEmpty) {
      val psize = pageSize.getOrElse(10)
      val pnr = pageNr.getOrElse(1)
      DelegateDBRepository.searchDelegateByName(companyId, psize, pnr, searchTerm).map(page => Json.toJson(page))
    } else {
      DelegateDBRepository.getDelegatesByCompanyId(companyId).map(delegates => Json.toJson(delegates))
    }
  }
}