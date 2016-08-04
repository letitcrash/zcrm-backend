package utils.converters

import models._
import database.tables.{ClientEntity, ContactProfileEntity}

object ClientConverter {

  implicit class ClientToEntity(client: Client) {
    def asClientEntity: ClientEntity  = {
      ClientEntity(id = client.id,
                   companyId = client.companyId,
                   contactProfileId = client.contactProfile.id.get)
    }

  }
  implicit class EntityToClient(tup: (ClientEntity, ContactProfileEntity)) {
    import utils.converters.ContactProfileConverter._
    def asClient: Client = {
      Client(id = tup._1.id,
             companyId = tup._1.companyId,
             contactProfile = tup._2.asProfile)
      }
  }

}
