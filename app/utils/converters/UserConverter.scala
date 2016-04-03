package utils.converters

import database.tables.{ContactProfileEntity, UserEntity}
import models.User

object UserConverter {
  
  implicit class EntityToUser (tuple: (UserEntity, ContactProfileEntity)) {

    import ContactProfileConverter.EntityToProfile

    def asUser: User = {
      models.User(
        id = tuple._1.id,
        userLevel = tuple._1.userLevel,
        username = tuple._1.username,
        contactProfile = Some(tuple._2.asProfile))
    }
  }

  implicit class UserToEntity(user: User) {

    def asEntity(profileId: Int): UserEntity = {
      UserEntity(
        id = user.id,
        username = user.username,
        userLevel = user.userLevel,
        profileId = Some(profileId))
    }
  }

  implicit class EntitiesToUsers
  [USERENTITY <: UserEntity,
  CONTACTENTITY <: ContactProfileEntity]
  (seq: Seq[(USERENTITY, CONTACTENTITY)]) {

    def asUserList: Seq[User] = seq map(_.asUser)
  }
}
