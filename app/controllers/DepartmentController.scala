package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import utils.ExpectedFormat._
import controllers.session.InsufficientRightsException
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database.DepartmentDBRepository 
import play.api.libs.json.Json
import scala.util.{Failure, Success, Try}
import scala.concurrent.Future

@Singleton
class DepartmentController @Inject() extends CRMController {
  import utils.JSFormat.departmentFrmt

  //TODO: add permissions check
  def newDepartment(companyId: Int) = CRMActionAsync[Department](expectedDepartmentFormat) { rq => 
    // if(rq.header.belongsToCompany(companyId)){
       DepartmentDBRepository.createDepartment(rq.body, companyId).map( department => Json.toJson(department))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }


  //TODO: add permissions check
  def updateDepartment(companyId: Int, departmentId: Int) = CRMActionAsync[Department](expectedDepartmentFormat){ rq =>
    // if(rq.header.belongsToCompany(companyId)){
      DepartmentDBRepository.updateDepartment(rq.body.copy(id = Some(departmentId)), companyId).map( department => Json.toJson(department))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }


  //TODO: add permissions check
  def getDepartment(companyId: Int, departmentId: Int) = CRMActionAsync { rq =>
    // if(rq.header.belongsToCompany(companyId)){
      DepartmentDBRepository.getDepartmentById(departmentId).map( department => Json.toJson(department))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }

  //TODO: add permissions check
  def getAllDepartments(companyId: Int) = CRMActionAsync { rq =>
    // if(rq.header.belongsToCompany(companyId)){
      DepartmentDBRepository.getDepartmentsByCompanyId(companyId).map( department => Json.toJson(department))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }
  
  def deleteDepartmentById(companyId: Int, departmentId:Int) = CRMActionAsync{rq =>
      DepartmentDBRepository.deleteDepartment(departmentId).map(deletedDepartment => Json.toJson(deletedDepartment))
  }

  def searchAllDepartmentsByName(companyId: Int, pageSize: Option[Int], pageNr: Option[Int], searchTerm: Option[String]) = CRMActionAsync{rq =>
    import utils.JSFormat._
    if (pageNr.nonEmpty || pageSize.nonEmpty || searchTerm.nonEmpty) {
      val psize = pageSize.getOrElse(10)
      val pnr = pageNr.getOrElse(1)
      DepartmentDBRepository.searchDepartmentByName(companyId, psize, pnr, searchTerm).map(page => Json.toJson(page))
    } else { DepartmentDBRepository.getDepartmentsByCompanyId(companyId).map( departments => Json.toJson(departments)) }
  }
 
}
