package database

import models.Delegate

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.{Delegate, DelegateGroup, PagedResult}
import utils.converters.DelegateConverter._



object DelegateDBRepository {

  import database.gen.current.dao._

  def createDelegate(delegate: Delegate, companyId: Int): Future[Delegate] = {
    insertDelegate(delegate.asDelegateEntity(companyId)).map(_.asDelegate)
  }

  def updateDelegate(delegate: Delegate, companyId: Int): Future[Delegate] = {
    updateDelegateEntity(delegate.asDelegateEntity(companyId)).map(_.asDelegate)
  }

  def deleteDelegate(delegateId: Int): Future[Delegate] = {
    deleteDelegateById(delegateId).map(_.asDelegate)
  }

  def getDelegatesByCompanyId(companyId: Int): Future[List[Delegate]] = {
    getDelegateEntitiesByCompanyId(companyId).map(_.map(_.asDelegate))
  }

  def getDelegateById(id: Int): Future[Delegate] = {
    getDelegateEntityById(id).map(_.asDelegate)
  }


  def addDelegateGroup(group: DelegateGroup): Future[Delegate] = {
    insertGroupDelegate(group.asGroupEntity).flatMap( group => 
        getDelegateEntityById(group.delegateId.get).map( delegateEntt =>
            (group, delegateEntt).asDelegate))
        
  }

  def searchDelegateByName(companyId: Int, pageSize: Int, pageNr: Int, searchTerm: Option[String]): Future[PagedResult[Delegate]] = {
    searchDelegateEntitiesByName(companyId, pageSize, pageNr, searchTerm).map{dbPage =>
        PagedResult[Delegate](pageSize = dbPage.pageSize,
                             pageNr = dbPage.pageNr,
                             totalCount = dbPage.totalCount,
                             data = dbPage.data.map(_.asDelegate))}
  }

}
