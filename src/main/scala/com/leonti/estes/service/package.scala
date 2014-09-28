package com.leonti.estes

import com.leonti.estes.dao.impl.IngredientDaoMongo
import com.leonti.estes.dao.impl._
import com.leonti.estes.domain.UserSession
import com.leonti.estes.domain.User
import com.leonti.estes.domain.Settings

package object service {

	object DishService extends DishDaoMongo
	object IngredientService extends IngredientDaoMongo
	object OrderService extends OrderDaoMongo
	object WaiterService extends WaiterDaoMongo
	object UserService extends UserDaoMongo {
		def getOrCreate(email: String): User = {
			getByEmail(email) match {
				case Some(user: User) => user
				case None => create(User(id = None, email = email))
			}
		}
	}
	object UserSessionService extends UserSessionDaoMongo {
		def createUserSession(user: User): UserSession = {
			val userSession = UserSession(id = java.util.UUID.randomUUID.toString, userId = user.id.get, created = System.currentTimeMillis)
			create(userSession);
		}
	}
	object SettingsService extends SettingsDaoMongo {
		def getOrCreate(userId: Long): Settings = {
			tryToGet(userId) match {
				case Some(settings: Settings) => settings
				case None => create(Settings(userId = userId, tax = 7, printer = "BROWSER", receiptWidth = 40))
			}
		}
	} 
}