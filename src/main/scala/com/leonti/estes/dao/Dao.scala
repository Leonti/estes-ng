package com.leonti.estes.dao

import com.leonti.estes.domain._

trait CompositeIdDao[T] {
	def create(parentId: Long, entity: T): T
	def update(parentId: Long, id: Long, entity: T): T
	def delete(parentId: Long, id: Long): T
	def get(parentId: Long, id: Long): T
	def getAll(parentId: Long): List[T]
}

trait Dao[T] {
	def create(entity: T): T
	def update(id: Long, entity: T): T
	def delete(id: Long): T
	def get(id: Long): T
	def getAll(): List[T]
}

abstract class ArticleDao extends CompositeIdDao[Article]
abstract class OrderDao extends CompositeIdDao[Order] {
	def getInRange(id: Long, from: Long, to: Long): List[Order]
}
abstract class WaiterDao extends CompositeIdDao[Waiter]
abstract class EventDao extends CompositeIdDao[Event]
abstract class UserDao extends Dao[User]

trait UserSessionDao {
	def create(userSession: UserSession): UserSession
	def delete(id: String)
	def get(id: String): UserSession
}

trait SettingsDao {
	def create(settings: Settings): Settings
	def update(userId: Long, settings: Settings): Settings
	def delete(userId: Long)
	def tryToGet(userId: Long): Option[Settings]
	def get(userId: Long): Settings
}

class NotFoundException(msg: String) extends Exception