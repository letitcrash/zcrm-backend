package database

import models.Employee

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.{Client, PagedResult}
import play.api.Logger
import utils.converters.ClientConverter._


object ClientDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  def createClient(client: Client): Future[Client] = {
    insertClient(client.asClientEntity)
          .flatMap(inserted => 
                   getClientWithProfileById(inserted.id.get).map(_.asClient))
  }

  def updateClient(client: Client): Future[Client] = {
    updateClientEntity(client.asClientEntity)
          .flatMap(updated => 
                   getClientWithProfileById(updated.id.get).map(_.asClient))
  }

  def deleteClient(clientId: Int): Future[Client] = {
    deleteClientById(clientId)
          .map(deleted => deleted.asClient)
  }

  def getClientById(id: Int): Future[Client] = {
    getClientWithProfileById(id).map(client => client.asClient)
  }

  def getClientsByCompanyId(companyId: Int): Future[List[Client]] = {
    getAllClientsWithContactProfileByCompanyId(companyId).map(list => list.map(_.asClient))
  } 
}
