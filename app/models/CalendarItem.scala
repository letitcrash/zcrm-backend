package models

import java.sql.Timestamp

case class CalendarItem (
                        subject: String,
                        body: String,
                        startDate: Timestamp,
                        endDate: Timestamp)
