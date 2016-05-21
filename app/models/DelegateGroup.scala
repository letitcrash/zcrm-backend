package models
import java.sql.Timestamp

case class DelegateGroup(delegateId: Option[Int], 
                         userId: Option[Int],
                         startDate: Option[Timestamp],
                         endDate: Option[Timestamp])
