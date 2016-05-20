package database

import database.tables.UserEntity
import exceptions.UsernameAlreadyExistException
import models.{ContactProfile, Employee, UserLevels,User}

import scala.util.{Failure, Success, Try}
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Logger
import utils.converters.EmployeeConverter._


object EmployeeDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

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


 def getEmployeeByUser(user: User): Future[Employee] = {
   getEmployeeWithUserByUserId(user.id.get).map(empl => empl.asEmployee)
 }

 def getEmployeesByCompanyId(companyId: Int): Future[List[Employee]] = {
   getAllEmployeesWithUsersByCompanyId(companyId).map(list => list.map(_.asEmployee))
 }

 def getAggragatedEmployeesByCompanyId(companyId: Int): Future[List[Employee]] = {
   getAllAggregatedEmployeesByCompanyId(companyId).flatMap( listAggEmployees =>
     Future.sequence(
       listAggEmployees.map( aggEmployee =>
           getDelegateEntitiesByUserId(aggEmployee._1._1._1._1._2._1.id.get).flatMap(
             delegatesTup =>
               getTeamEntitiesByUserId(aggEmployee._1._1._1._1._2._1.id.get).map(
                 teamsTup => 
                   aggEmployee.asEmployee(teamsTup, delegatesTup))))))
 }

 def getEmployeeByEmployeeId(employeeId: Int): Future[Employee] = {
   getEmployeeWithUserById(employeeId).map(empl => empl.asEmployee)
 }

  def updateEmployee(employee: Employee): Future[Employee] = {
    updateEmployeeWithUser(employee.asEmployeeEntity).map(updatedEmpl => updatedEmpl.asEmployee)
  }

  def softDeleteEmployeeById(employeeId: Int): Future[Employee] = {
      for{
          deleted <- softDeleteEmployeeEntityById(employeeId)
          userWithProfileEntt <- getUserWithProfileByUserId(deleted.userId)
      } yield(deleted, userWithProfileEntt).asEmployee
  }
}
