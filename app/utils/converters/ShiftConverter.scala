package utils.converters

import database.tables.ShiftEntity
import models.Shift

object ShiftConverter {
  
  implicit class EntityToShift (s: ShiftEntity) {
      def asShift: Shift= {
              Shift(id = s.id,
                    name = s.name)    
      }
  }

  implicit class ShiftToEntity(s: Shift){
      def asShiftEntity(companyId: Int): ShiftEntity = {
              ShiftEntity(id = s.id,
                         companyId = companyId,
                         name = s.name) 
      }
  }
}


