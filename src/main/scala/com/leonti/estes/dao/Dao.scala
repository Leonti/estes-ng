package com.leonti.estes.dao

import com.leonti.estes.domain._

trait CompositeIdDao[T] {
	def create(entity: T): T
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

abstract class DishDao extends CompositeIdDao[Dish]
abstract class IngredientDao extends CompositeIdDao[Ingredient]
abstract class OrderDao extends CompositeIdDao[Order]
abstract class WaiterDao extends CompositeIdDao[Waiter]
abstract class UserDao extends Dao[User] {
	def getOrCreateUser(email: String): User
}

trait UserSessionDao {
	def create(userSession: UserSession): UserSession
	def delete(id: String)
	def get(id: String): UserSession
}

class NotFoundException(msg: String) extends Exception