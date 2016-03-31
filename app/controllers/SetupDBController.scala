package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import play.api.libs.json.{Json, JsValue}

@Singleton
class SetupDBController @Inject() (configuration: Configuration) extends CRMController {

  def setupDataBase = Action{
    Logger.info("Trying to setup DB...") 
    val wipeDatabase = configuration.getBoolean("wipeDatabase").getOrElse(false)
    if(wipeDatabase){
      dbb.gen.current.initializeDatabase()
      Ok(views.html.index("DB is ready."))
    }else{
      Logger.info("No access to setup DB.")
      Ok(views.html.index("No access to setup DB."))
    }

  }

}


