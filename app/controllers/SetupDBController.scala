package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import play.api.libs.json.{Json, JsValue}

//@Singleton
//class SetupDBController @Inject() (configuration: Configuration) extends CRMController {
//
//  def setupDataBase = Action {
//    Logger.info("Trying to setup DB...") 
//
//    val wipeDatabase = configuration.getBoolean("wipeDatabase").getOrElse(false)
//
//    if (wipeDatabase) {
//      database.gen.current.initializeDatabase()
//      Ok("DB is ready.\n")
//    } else {
//      Ok("No access to setup DB.\n")
//    }
//  }
//}