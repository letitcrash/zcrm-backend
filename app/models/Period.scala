package models

import java.sql.Timestamp

case class Period(id: Option[Int] = None,
                  //previousPeriod: Option[Period] = None,
                  //user: User,
                  start: Timestamp,
                  end: Option[Timestamp] = None,
                  status: Int) //0 - Started, 1 - Paused, 2 - Stopped
