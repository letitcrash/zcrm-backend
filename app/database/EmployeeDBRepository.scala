package database

import database.tables.UserEntity
import exceptions.UsernameAlreadyExistException
import models.{ContactProfile, Employee, UserLevels}

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext


object EmployeeDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  /*
  def createNewEmployee(username: String, contactProfile: ContactProfile, employee: Employee): Future[Employee] = {
    import utils.converters.ContactProfileConverter.ContactProfileToEntity
    import utils.converters.EmployeeConverter._

    UserDBRepository.getUserByUsername(username).map(usr =>  Future(new UsernameAlreadyExistException))

      for {
        profEnt <- insertProfile(contactProfile.asEntity())
        userEnt <- insertUser(UserEntity(username = username, userLevel = UserLevels.USER, profileId = profEnt.id.get))
        empEnt <- insertEmployee(employee.asEmployeeEntity(employee.companyId, userEnt.id.get))
      } yield (empEnt, userEnt, profEnt).asEmployee()
    }
  */


 def addEmployee(employee: Employee): Future[Employee] = {
   import utils.converters.EmployeeConverter._
   import utils.converters.UserConverter._

   for {
     employeeEntt <- upsertEmployee(employee.asEmployeeEntity)
     userWithProfileEntt <- getUserWithProfileByUserId(employee.user.get.id.get) 
   } yield (employeeEntt, userWithProfileEntt).asEmployee

   Future{employee}
 }
  
}