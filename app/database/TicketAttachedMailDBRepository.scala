package database

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.{TicketAction, ActionAttachedMail , PagedResult}
import play.api.Logger
import utils.converters.ActionConverter._


object TicketAttachedMailDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  def createAttchedMailAction(action: ActionAttachedMail): Future[ActionAttachedMail] = {
    insertAttachedMailEnitity(action.asAttachedActionEntity)
          .map(inserted => inserted.asAttachedMailAction)
  }

}
