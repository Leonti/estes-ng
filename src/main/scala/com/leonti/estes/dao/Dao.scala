package com.leonti.estes.dao

import com.leonti.estes.domain.Dish
import com.leonti.estes.domain.Ingredient
import com.leonti.estes.domain.Order
import com.leonti.estes.domain.Waiter

trait Dao[T] {
	def create(entity: T): T
	def update(id: Long, entity: T): T
	def delete(id: Long): T
	def get(id: Long): T
}

abstract class DishDao extends Dao[Dish]
abstract class IngredientDao extends Dao[Ingredient]
abstract class OrderDao extends Dao[Order]
abstract class WaiterDao extends Dao[Waiter]

class NotFoundException(msg: String) extends Exception