package com.leonti.estes.dao.impl

import com.leonti.estes.config.Configuration
import com.leonti.estes.dao.Dao
import com.leonti.estes.dao.DatabaseClient
import com.leonti.estes.dao.DishDao
import com.leonti.estes.dao.NotFoundException
import com.leonti.estes.domain.Dish
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._

class DishDaoMongo extends DishDao with Configuration with IdGeneratorMongo {
	val table = "dish"
	lazy val coll: MongoCollection = DatabaseClient.db(table)
	
	def create(dish: Dish): Dish = {
		val withId = dish.copy(id = Some(generateId(table)))
		coll.insert(grater[Dish].asDBObject(withId))
		withId
	}
	
	def update(id: Long, dish: Dish): Dish = {
		coll.update(toId(id), grater[Dish].asDBObject(dish))
		dish
	}
	
	def delete(id: Long) = {
		val dish = get(id)
		coll.remove(toId(id))
		dish
	}
	
	def get(id: Long): Dish = {
		coll.findOne(toId(id)) match {
			case Some(dbo) => grater[Dish].asObject(dbo)
			case None => throw new NotFoundException("Dish")
		}
	}
	
	def getAll(): List[Dish] = {
		coll.map(dbo => grater[Dish].asObject(dbo)).toList
	}
}