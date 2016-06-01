package database

import database.tables.UserEntity
import exceptions.UsernameAlreadyExistException
import models._

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
    import utils.converters.TeamConverter._
    import utils.converters.DelegateConverter._

    //TODO: should be transactionally
    isUserExists(username).flatMap( flag =>
        if(flag){
          throw new UsernameAlreadyExistException
        } else {
         (for {
            profEnt <- insertProfile(contactProfile.asEntity())
            userEnt <- insertUser(UserEntity(username = username, userLevel = UserLevels.USER, profileId = profEnt.id.get))
            empEnt <- insertEmployee(employee.asEmployeeEntity(employee.companyId, userEnt.id.get))
            teamGrpEnts <- employee.teams match { case Some(teams) => insertTeamGroups(teams.map(t =>t.asTeamGroup(userEnt.id.get)).map(_.asEntity))
                                                 case _ => Future(List())}
            delegateGrpEnts <- employee.delegates match { case Some(delegates) => insertDelegateGroups(delegates.map(d => d.asDelegateGroup(userEnt.id)).map(_.asGroupEntity))
                                                         case _ => Future(List())}
         } yield (empEnt, userEnt, profEnt).asEmployee()).map( e =>
            e.copy( position = employee.position,
                    shift = employee.shift,
                    department = employee.department,
                    union = employee.union,
                    teams = employee.teams,
                    delegates = employee.delegates))
    })
 }


  def getEmployeeByUser(user: User): Future[Employee] = {
    getEmployeeWithUserByUserId(user.id.get).map(empl => empl.asEmployee)
  }

  def getEmployeesByCompanyId(companyId: Int): Future[List[Employee]] = {
    getAllEmployeesWithUsersByCompanyId(companyId).map(list => list.map(_.asEmployee))
  }

  def getAggragatedEmployeesByCompanyId(companyId: Int,
                                        positionIds: List[Int],
                                        shiftIds: List[Int],
                                        departmentIds: List[Int],
                                        unionIds:List[Int],
                                        delegateIds: List[Int],
                                        teamIds: List[Int]): Future[List[Employee]] = {
    getAllAggregatedEmployeesByCompanyId(companyId,
                                         positionIds,
                                         shiftIds,
                                         departmentIds,
                                         unionIds,
                                         delegateIds,
                                         teamIds).flatMap( listAggEmployees =>
      Future.sequence(
        listAggEmployees.map( aggEmployee =>
            getDelegateEntitiesByUserId(aggEmployee._1._1._1._1._2._1.id.get).flatMap(
              delegatesTup =>
                getTeamEntitiesByUserId(aggEmployee._1._1._1._1._2._1.id.get).map(
                  teamsTup => 
                    aggEmployee.asEmployee(teamsTup, delegatesTup))))))
  }

  def searchAggragatedEmployeesByCompanyId (companyId: Int,
                                            positionIds: List[Int],
                                            shiftIds: List[Int],
                                            departmentIds: List[Int],
                                            unionIds:List[Int],
                                            delegateIds: List[Int],
                                            teamIds: List[Int],
                                            pageSize: Int,
                                            pageNr: Int,
                                            searchTerm: Option[String]): Future[PagedResult[Employee]] = {
    searchAllAggregatedEmployeesByCompanyId(companyId, 
                                            shiftIds,
                                            departmentIds,
                                            unionIds,
                                            delegateIds,
                                            teamIds,
                                            positionIds, 
                                            pageSize, 
                                            pageNr, 
                                            searchTerm).flatMap( dbPage =>
      Future.sequence(
        dbPage.data.map( aggEmployee =>
            getDelegateEntitiesByUserId(aggEmployee._1._1._1._1._2._1.id.get).flatMap(
              delegatesTup =>
                getTeamEntitiesByUserId(aggEmployee._1._1._1._1._2._1.id.get).map(
                  teamsTup => 
                    aggEmployee.asEmployee(teamsTup, delegatesTup))))).map( empList => 
                      PagedResult[Employee](
                        pageSize = dbPage.pageSize,
                        pageNr = dbPage.pageNr,
                        totalCount = dbPage.totalCount,
                        data = empList)))

  }

  def getEmployeeByEmployeeId(employeeId: Int): Future[Employee] = {
    getEmployeeWithUserById(employeeId).map(empl => empl.asEmployee)
  }


  def updateEmployeeContactProfile(profile: ContactProfile): Future[ContactProfile] = {
    import utils.converters.ContactProfileConverter._
    updateProfile(profile.asEntity()).map(cp => cp.asProfile)
  }

  //TODO: should be transactionally
  def updateEmployee(employee: Employee): Future[Employee] = {
    import utils.converters.TeamConverter._
    import utils.converters.DelegateConverter._
    (for {
      //teamGrpsDel <-  deleteTeamGroupByUserId(employee.user.get.id.get)
      //delegateGrpsDel <- deleteGroupDelegateByUserId(employee.user.get.id.get)
      employeeUpd <- updateEmployeeWithUser(employee.asEmployeeEntity)
      //teamGrpEnt <- employee.teams match { case Some(teams) => insertTeamGroups(teams.map(t => t.asTeamGroup(employeeUpd._2._1.id.get)).map(_.asEntity)) case _ => Future(List())}
      //delegateGrpEnt <- employee.delegates match { case Some(delegates) => insertDelegateGroups(delegates.map(d => d.asDelegateGroup(employeeUpd._2._1.id)).map(_.asGroupEntity)) case _ => Future(List())}
    } yield employeeUpd.asEmployee()).map( e =>
            e.copy( position = employee.position,
                    shift = employee.shift,
                    department = employee.department,
                    union = employee.union,
                    teams = employee.teams,
                    delegates = employee.delegates))
  }

  def softDeleteEmployeeById(employeeId: Int): Future[Employee] = {
      for{
          deleted <- softDeleteEmployeeEntityById(employeeId)
          userWithProfileEntt <- getUserWithProfileByUserId(deleted.userId)
      } yield(deleted, userWithProfileEntt).asEmployee
  }

  def updateEmployeePosition(employeeId: Int, positionId: Int): Future[Position] = {
    import utils.converters.PositionConverter._
    updateEmployeeEntityPosition(employeeId, Some(positionId)).map(_.asEmployee)
    getPositionEntityById(positionId).map(_.asPosition)
  }

  def clearEmployeePosition(employeeId: Int, positionId: Int): Future[Position] = {
    import utils.converters.PositionConverter._
    updateEmployeeEntityPosition(employeeId, None).map(_.asEmployee)
    getPositionEntityById(positionId).map(_.asPosition)
  }

  def updateEmployeeShift(employeeId: Int, shiftId: Int): Future[Shift] = {
    import utils.converters.ShiftConverter._
    updateEmployeeEntityShift(employeeId, Some(shiftId)).map(_.asEmployee)
    getShiftEntityById(shiftId).map(_.asShift)
  }

  def clearEmployeeShift(employeeId: Int, shiftId: Int): Future[Shift] = {
    import utils.converters.ShiftConverter._
    updateEmployeeEntityShift(employeeId, None).map(_.asEmployee)
    getShiftEntityById(shiftId).map(_.asShift)
  }

  def updateEmployeeDepartment(employeeId: Int, departmentId: Int): Future[Department] = {
    import utils.converters.DepartmentConverter._
    updateEmployeeEntityDepartment(employeeId, Some(departmentId)).map(_.asEmployee)
    getDepartmentEntityById(departmentId).map(_.asDepartment)
  }

  def clearEmployeeDepartment(employeeId: Int, departmentId: Int): Future[Department] = {
    import utils.converters.DepartmentConverter._
    updateEmployeeEntityDepartment(employeeId, None).map(_.asEmployee)
    getDepartmentEntityById(departmentId).map(_.asDepartment)
  }

  def updateEmployeeUnion(employeeId: Int, unionId: Int): Future[Union] = {
    import utils.converters.UnionConverter._
    updateEmployeeEntityUnion(employeeId, Some(unionId)).map(_.asEmployee)
    getUnionEntityById(unionId).map(_.asUnion)
  }

  def clearEmployeeUnion(employeeId: Int, unionId: Int): Future[Union] = {
    import utils.converters.UnionConverter._
    updateEmployeeEntityUnion(employeeId, None).map(_.asEmployee)
    getUnionEntityById(unionId).map(_.asUnion)
  }

  def updateEmployeeDelegate(employeeId: Int, delegates: List[Delegate]): Future[List[Delegate]] = {
    import utils.converters.DelegateConverter._
    for {
     userId <- getUserIdByEmloyeeId(employeeId)
     delegateGrpsDel <- deleteGroupDelegateByUserId(userId)
     delegateGrpEnt <-  insertDelegateGroups(delegates.map(d => d.asDelegateGroup(Some(userId))).map(_.asGroupEntity))
    } yield delegates 
  }

  def clearEmployeeDelegate(employeeId: Int): Future[List[DelegateGroup]] = {
    import utils.converters.DelegateConverter._
    for {
     userId <- getUserIdByEmloyeeId(employeeId)
     delegates <- getDelegateEntitiesByUserId(userId)
     delegateGrpsDel <- deleteGroupDelegateByUserId(userId)
    } yield delegates.map(_._1.asDelegateGroup) 
  }

  def updateEmployeeTeam(employeeId: Int, teams: List[Team]): Future[List[Team]] = {
    import utils.converters.TeamConverter._
    for {
     userId <- getUserIdByEmloyeeId(employeeId)
     teamGrpsDel <-  deleteTeamGroupByUserId(userId)
     teamGrpEnt <- insertTeamGroups(teams.map(t => t.asTeamGroup(userId)).map(_.asEntity))
    } yield teams 
  }

  def clearEmployeeTeam(employeeId: Int): Future[List[TeamGroup]] = {
    import utils.converters.TeamConverter._
    for {
     userId <- getUserIdByEmloyeeId(employeeId)
     teams <- getTeamEntitiesByUserId(userId)
     teamGrpsDel <- deleteTeamGroupByUserId(userId)
    } yield teams.map(_._1.asTeamGroup) 
  }

}
