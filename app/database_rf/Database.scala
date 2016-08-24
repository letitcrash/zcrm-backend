package database_rf

import play.api.db.slick.DatabaseConfigProvider
import javax.inject._
import play.api.mvc.Controller
import slick.driver.JdbcProfile
import controllers.CRMController

@Singleton class Database @Inject() (dbConfigProvider: DatabaseConfigProvider) extends Controller {
  val config = dbConfigProvider.get[JdbcProfile]
  val instance = config.db
}