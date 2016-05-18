package utils.converters

import database.tables.DepartmentEntity
import models.Department

object DepartmentConverter {
  
  implicit class EntityToDepartment (d: DepartmentEntity) {
      def asDepartment: Department= {
              Department(id = d.id,
                   			 name = d.name)
      }
  }

  implicit class DepartmentToEntity(d: Department){
      def asDepartmentEntity(companyId: Int): DepartmentEntity = {
              DepartmentEntity(id = d.id,
                         			 companyId = companyId,
                         			 name = d.name)
      }
  }
}


