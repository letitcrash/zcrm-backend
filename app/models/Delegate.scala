package models
import java.sql.Timestamp

case class Delegate(id: Option[Int] = None, 
                    name: String,
                    startDate: Option[Timestamp] = None,
                    endDate: Option[Timestamp] = None)
