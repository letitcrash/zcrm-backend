package models
import java.sql.Timestamp

case class Team(id: Option[Int] = None,
                name: String,
                description: Option[String] = None,
                startDate: Option[Timestamp] = None,
                endDate: Option[Timestamp] = None)
