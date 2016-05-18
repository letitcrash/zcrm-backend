package utils.converters

import models.Position
import database.tables.PositionEntity

object PositionConverter{
  implicit class EntitiesToPosition[T <: PositionEntity](positionEntt: T) {
    def asPosition = {
      Position(
        id = positionEntt.id,
        name = positionEntt.name
      )
    }
  }

  implicit class PositionToEntity[ T <: Position](position: T) {
    def asPositionEntity(companyId: Int) = {
      PositionEntity(
        id = position.id,
        companyId = companyId,
        name = position.name
      )
    }
  }
}
