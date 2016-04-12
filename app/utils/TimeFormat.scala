package utils

import java.sql.Timestamp
import java.text.SimpleDateFormat

object TimeFormat {

  private val df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

  def timestampToString(ts: Timestamp) = df.format(ts)

}
