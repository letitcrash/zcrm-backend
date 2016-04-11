package database

import database.tables.UserEntity
import exceptions.UsernameAlreadyExistException
import models.{ContactProfile, Employee, UserLevels,User}

import scala.util.{Failure, Success, Try}
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Logger


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


 def createEmployee(username: String, contactProfile: ContactProfile, employee: Employee): Future[Employee] = {
    import utils.converters.ContactProfileConverter.ContactProfileToEntity
    import utils.converters.EmployeeConverter._

    //TODO: should be transactionally
    isUserExists(username).flatMap( flag =>
        if(flag){
          throw new UsernameAlreadyExistException
        } else {
          for {
            profEnt <- insertProfile(contactProfile.asEntity())
            userEnt <- insertUser(UserEntity(username = username, userLevel = UserLevels.USER, profileId = profEnt.id.get))
            empEnt <- insertEmployee(employee.asEmployeeEntity(employee.companyId, userEnt.id.get))
          } yield (empEnt, userEnt, profEnt).asEmployee()
    })
  }

  
}
