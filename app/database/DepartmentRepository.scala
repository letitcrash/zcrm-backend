package database

import models.Employee

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.{Department, PagedResult}
import play.api.Logger
import utils.converters.DepartmentConverter._


object DepartmentDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  def createDepartment(department: Department, companyId: Int): Future[Department] = {
    insertDepartment(department.asDepartmentEntity(companyId))
          .map(inserted => inserted.asDepartment)
  }

  def updateDepartment(department: Department, companyId: Int): Future[Department] = {
    updateDepartmentEntity(department.asDepartmentEntity(companyId))
          .map(updated => updated.asDepartment)
  }

  def deleteDepartment(departmentId: Int): Future[Department] = {
    softDeleteDepartmentById(departmentId)
          .map(deleted => deleted.asDepartment)
  }

  def getDepartmentById(id: Int): Future[Department] = {
    getDepartmentEntityById(id).map(department => department.asDepartment)
  }

  def getDepartmentsByCompanyId(companyId: Int): Future[List[Department]] = {
    getDepartmentEntitiesByCompanyId(companyId).map(list => list.map(_.asDepartment))
  } 
  
  def searchDepartmentByName(companyId: Int, pageSize: Int, pageNr: Int, searchTerm: Option[String]): Future[PagedResult[Department]] = {
    searchDepartmentEntitiesByName(companyId, pageSize, pageNr, searchTerm).map{dbPage =>
        PagedResult[Department](pageSize = dbPage.pageSize,
                             pageNr = dbPage.pageNr,
                             totalCount = dbPage.totalCount,
                             data = dbPage.data.map(_.asDepartment))}
  }
}
