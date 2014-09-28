package com.leonti.estes

import com.leonti.estes.dao.impl.IngredientDaoMongo
import com.leonti.estes.dao.impl._
import com.leonti.estes.domain.UserSession
import com.leonti.estes.domain.User

package object service {
	
	object DishService extends DishDaoMongo
	object IngredientService extends IngredientDaoMongo 
	object OrderService extends OrderDaoMongo
	object WaiterService extends WaiterDaoMongo
	object UserService extends UserDaoMongo
	object UserSessionService extends UserSessionDaoMongo {
		def createUserSession(user: User): UserSession = {
			val userSession = UserSession(id = java.util.UUID.randomUUID.toString, userId = user.id.get, created = System.currentTimeMillis)
			create(userSession);
		}
	}
}