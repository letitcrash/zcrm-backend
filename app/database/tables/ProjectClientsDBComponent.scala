package database.tables

import java.sql.Timestamp
import models.RowStatus
import slick.model.ForeignKeyAction._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.profile.SqlProfile.ColumnOption.Nullable
import database.PagedDBResult

case class ProjectClientEntity(
    projectId: Int,
    clientId: Int)

trait ProjectClientDBComponent extends DBComponent {
    this: DBComponent 
    with ProjectDBComponent
    with ClientDBComponent => 

  import dbConfig.driver.api._

  val projectClients = TableQuery[ProjectClientTable]
  
  class ProjectClientTable(tag: Tag) extends Table[ProjectClientEntity](tag, "tbl_project_clients") {
    def projectId = column[Int]("project_id")
    def clientId = column[Int]("client_id")    

    def fkProjectId = foreignKey("fk_project_clients_project", projectId, projects)(_.id)
    def fkClientIf = foreignKey("fk_project_clients_client", clientId, clients)(_.id)

    def * = (projectId,clientId)<>(ProjectClientEntity.tupled, ProjectClientEntity.unapply)
  }


  
  //CRUD ProjectClientEntity
  def insertProjectClient(projectClient: ProjectClientEntity): Future[ProjectClientEntity] = {
    db.run(projectClients  += projectClient)
    Future(projectClient)
  }

  def getClientEntitiesByProjectId(projectId: Int): Future[List[(ClientEntity, ContactProfileEntity)]] = {
    db.run(projectClients.filter(_.projectId === projectId).result).flatMap(list =>
    Future.sequence(list.map(client => getClientWithProfileById(client.clientId)).toList))
  }

  def deleteProjectClient(projectId: Int, clientId:Int): Future[ProjectClientEntity] = {
    val deleted = db.run(projectClients.filter(p => (p.projectId === projectId && p.clientId == clientId)).result.head)
    db.run(projectClients.filter(p => (p.projectId === projectId && p.clientId == clientId)).delete)
    deleted
  }

  def deleteProjectClient(entity: ProjectClientEntity): Future[ProjectClientEntity] = {
    db.run(projectClients.filter( t => ( t.projectId === entity.projectId &&
                                        t.clientId === entity.clientId)).delete)
    Future(entity)
  }

  def insertProjectClients(projectClients: List[ProjectClientEntity]): Future[List[ProjectClientEntity]] = {
    Future.sequence(projectClients.map(c =>  insertProjectClient(c)))
  }

  def insertProjectClientEntities(clients: List[ClientEntity], projectId: Int): Future[List[ProjectClientEntity]] = {
    Future.sequence(clients.map(c =>  insertProjectClient(ProjectClientEntity(c.id.get, projectId))))
  }

  def deleteProjectMembers(projectClients : List[ProjectClientEntity]): Future[List[ProjectClientEntity]] = {
    Future.sequence(projectClients.map( t =>  deleteProjectClient(t)))
    Future(projectClients)
  }

  def deleteAllClientsByProjectId(projectId: Int): Future[Int] = {
    db.run(projectClients.filter(t => ( t.projectId === projectId)).delete)
  }
}

