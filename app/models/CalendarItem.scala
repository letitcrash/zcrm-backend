package models

import java.sql.Timestamp

case class CalendarItem (title: String,
                         start: Timestamp,
                         end: Timestamp)
