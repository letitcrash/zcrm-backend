package utils.converters

import models.Position
import database.tables.PositionEntity

object PositionConverter{
  implicit class EntitiesToPosition[T <: PositionEntity](positionEntt: T) {
    def asPosition = {
      Position(
        id = Some(positionEntt.id.get),
        name = positionEntt.name
      )
    }
  }

  implicit class PositionToEntity[ T <: Position](position: T) {
    def asPositionEntity(companyId: Int) = {
      PositionEntity(
        companyId = companyId,
        name = position.name
      )
    }
  }
}
