package database

import database.tables.UserEntity
import exceptions.UsernameAlreadyExistException
import models.{ContactProfile, Employee, UserLevels,User}

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext


object EmployeeDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

 def getEmployeesByUser(user: User): Future[Employee] = {
   import utils.converters.EmployeeConverter._
   for {
     employeeEntt <- getEmployeeByUserId(user.id.get)
     userWithProfileEntt <- getUserWithProfileByUserId(user.id.get) 
   } yield (employeeEntt, userWithProfileEntt).asEmployee

 }

 def addEmployee(employee: Employee): Future[Employee] = {
   import utils.converters.EmployeeConverter._
   import utils.converters.UserConverter._

   for {
     employeeEntt <- upsertEmployee(employee.asEmployeeEntity)
     userWithProfileEntt <- getUserWithProfileByUserId(employee.user.get.id.get) 
   } yield (employeeEntt, userWithProfileEntt).asEmployee

 }
  
}
