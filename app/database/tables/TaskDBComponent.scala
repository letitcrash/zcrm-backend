package database.tables

import java.security.SecureRandom
import java.sql.Timestamp
import slick.profile.SqlProfile.ColumnOption.Nullable
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.model.ForeignKeyAction.{Cascade, SetNull, Restrict}
import java.math.BigInteger
import java.security.SecureRandom
import models._


case class TaskEntity(
  id: Option[Int] = None, 
  companyId: Int,
  createdByUserId: Int,
  assignedToUserId: Option[Int] = None,
  title: String,
  description: Option[String] = None,
  status: String = TaskStatus.NEW,
  //TODO: should be Date()
  dueDate: Option[String],
  createdAt: Timestamp = new Timestamp(System.currentTimeMillis()),
  updatedAt: Timestamp = new Timestamp(System.currentTimeMillis()),
  //TODO: rename RowStatus -> RecrodStatus
  recordStatus: String = RowStatus.ACTIVE
  )

trait TaskDBComponent extends DBComponent {
 this: DBComponent 
  with CompanyDBComponent
  with ContactProfileDBComponent
  with UserDBComponent =>

  import dbConfig.driver.api._

  val tasks = TableQuery[TaskTable]

  class TaskTable(tag: Tag) extends Table[TaskEntity](tag, "tbl_task") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def companyId = column[Int]("company_id")
    def createdByUserId = column[Int]("created_by_user_id")
    def assignedToUserId = column[Int]("assigned_to_user_id", Nullable)
    def title = column[String]("title")
    def description = column[String]("description", Nullable)
    def status = column[String]("status",O.Default(TaskStatus.NEW))
    def dueDate = column[String]("due_date_ts", Nullable)
    //TODO: check TS on DB
    def createdAt = column[Timestamp]("created_at", O.Default(new Timestamp(System.currentTimeMillis())))
    def updatedAt = column[Timestamp]("updated_at", O.Default(new Timestamp(System.currentTimeMillis())))
    def recordStatus = column[String]("record_status",O.Default(RowStatus.ACTIVE))
 
    //TODO: change onDelete = Cascade 
    def fkCreatedByUserId = foreignKey("fk_task_created_user", createdByUserId, users)(_.id, onUpdate = Restrict, onDelete = Cascade ) 
    def fkAssignedToUserId = foreignKey("fk_task_assigned_to_user", assignedToUserId, users)(_.id, onUpdate = Restrict, onDelete = Cascade ) 
    def fkCompanyId = foreignKey("fk_task_company", companyId, companies)(_.id, onUpdate = Restrict, onDelete = Cascade ) 
    //def fkAttachedMailId = foreignKey("fk_task_attached_mail", attachedMailId, taskAttachedMails)(_.id, onUpdate = Restrict, onDelete = Cascade) 

    def * = (id.?, companyId, createdByUserId, assignedToUserId.?, title, description.?, status, dueDate.?, createdAt, updatedAt, recordStatus) <>(TaskEntity.tupled, TaskEntity.unapply)
  }

  def tasksWithUsersWihtProfile = 
    tasks join usersWithProfile on (_.createdByUserId === _._1.id) joinLeft  usersWithProfile on (_._1.assignedToUserId === _._1.id)


  //TaskEntity CRUD
  def insertTaskEntity(task: TaskEntity): Future[TaskEntity] = {
      db.run(((tasks returning tasks.map(_.id) 
                into ((task,id) => task.copy(id=Some(id)))) += task))
  }

  def getTaskEntityById(id: Int): Future[TaskEntity] = {
      db.run(tasks.filter(_.id === id).result.head)
  }

  def getTaskEntitiesByCompanyId(companyId: Int): Future[List[TaskEntity]] = {
    db.run(tasks.filter(_.companyId === companyId).result).map(_.toList)
  }


  def getTasksWitUserByCompanyId(companyId: Int):
    Future[List[((TaskEntity, (UserEntity, ContactProfileEntity)) ,Option[(UserEntity, ContactProfileEntity)])]] = {
    db.run(tasksWithUsersWihtProfile.filter(t =>(t._1._1.companyId === companyId && 
                                                 t._1._1.recordStatus === RowStatus.ACTIVE)).result).map(_.toList)
  }

  def getTaskWitUserById(taskId: Int):
    Future[((TaskEntity, (UserEntity, ContactProfileEntity)) ,Option[(UserEntity, ContactProfileEntity)])] = {
    db.run(tasksWithUsersWihtProfile.filter(t => (t._1._1.id === taskId && 
                                                  t._1._1.recordStatus === RowStatus.ACTIVE)).result.head)
  }

  def updateTaskEntity(task: TaskEntity): Future[TaskEntity] = {
      db.run(tasks.filter(_.id === task.id).update(task))
        .map( num => task)
  }


  def softDeleteTaskEntityById(taskId: Int): Future[TaskEntity] = {
    getTaskEntityById(taskId).flatMap(res =>
        updateTaskEntity(res.copy(recordStatus = RowStatus.DELETED, 
                                  updatedAt = new Timestamp(System.currentTimeMillis()))))

  }

  //TaskEntity Filters
  def updateTaskWithUserByEntity(task: TaskEntity):
    Future[((TaskEntity, (UserEntity, ContactProfileEntity)) ,Option[(UserEntity, ContactProfileEntity)])] = {
    updateTaskEntity(task).flatMap( taskEntt =>
        getTaskWitUserById(taskEntt.id.get))
  }

  def upsertTaskEntity(task: TaskEntity): Future[TaskEntity] = {
    if(task.id.isDefined) {
      updateTaskEntity(task)
    } else {
      insertTaskEntity(task)
    }
  }
  
}

