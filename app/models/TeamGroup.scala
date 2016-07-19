package models
import java.sql.Timestamp

case class TeamGroup(teamId: Int, 
                     userId: Int,
                     startDate: Option[Timestamp] = None,
                     endDate: Option[Timestamp] = None)
