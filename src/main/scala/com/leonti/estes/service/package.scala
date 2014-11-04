package com.leonti.estes

import com.leonti.estes.dao.impl._
import com.leonti.estes.domain.UserSession
import com.leonti.estes.domain.User
import com.leonti.estes.domain.Order
import com.leonti.estes.domain.Settings
import com.leonti.estes.domain.PatchRequest
import com.leonti.estes.domain.OrderArticle
import com.leonti.estes.domain.Event

package object service {

	object ArticleService extends ArticleDaoMongo

	object OrderService extends OrderDaoMongo {
		def createOrder(userId: Long, order: Order): Order = {
			val processed = order.copy(dayId = Some(1), submitted = Some(System.currentTimeMillis))

			create(userId, processed)
		}

		// for now the only patch request for order is to set article status to prepared
		def patchOrder(userId: Long, orderId: Long, patchRequest: PatchRequest): Order = {
			val order = get(userId, orderId)

			val pattern = "/article/(.*)/status".r

			val articleId: Option[String] = for {
				pattern(id) <- pattern findFirstIn patchRequest.path
			} yield id

			val articles = order.articles.map(article => {
				println(article)
				if (article.id == articleId.get) article.copy(status = patchRequest.value)
				else article
			})

			update(userId, orderId, order.copy(articles = articles, status = getOrderStatus(articles)))
		}

		private def getOrderStatus(articles: List[OrderArticle]): String = {
			articles.map(article => {
				article.status
			}).reduceLeft[String]((acc, status) => {
				if (acc == "PREPARATION" || status == "PREPARATION") "PREPARATION"
				else "PREPARED"
			})
		}
	}
	
	object WaiterService extends WaiterDaoMongo
	object EventService extends EventDaoMongo {
		
		def patchEvent(userId: Long, eventId: Long, patchRequest: PatchRequest): Event = {
			val event = get(userId, eventId)
			update(userId, eventId, event.copy(ack = patchRequest.value :: event.ack))
		}
		
	}
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
				case None => create(Settings(userId = userId, printer = "BROWSER", receiptWidth = 40))
			}
		}
	}
}