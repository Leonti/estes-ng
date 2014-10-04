package com.leonti.estes.dao.impl

import com.leonti.estes.config.Configuration
import com.leonti.estes.dao.Dao
import com.leonti.estes.dao.DatabaseClient
import com.leonti.estes.dao.DishDao
import com.leonti.estes.dao.NotFoundException
import com.leonti.estes.domain.Ingredient
import com.leonti.estes.domain.Dish
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._
import scala.collection.JavaConversions._

class DishDaoMongo extends DishDao with Configuration with IdGeneratorMongo {
	val table = "dish"
	lazy val coll: MongoCollection = DatabaseClient.db(table)
	
	def create(userId: Long, dish: Dish): Dish = {
		val withId = dish.copy(id = Some(generateId(table, userId)))		
		coll.insert(grater[Dish].asDBObject(withId))
		withId
	}
	
	def update(userId: Long, id: Long, dish: Dish): Dish = {
		coll.update(toCompositeId(userId, id), grater[Dish].asDBObject(dish))
		dish
	}
	
	def delete(userId: Long, id: Long) = {
		val dish = get(userId, id)
		coll.remove(toCompositeId(userId, id))
		dish
	}
	
	def get(userId: Long, id: Long): Dish = {
		coll.findOne(toCompositeId(userId, id)) match {
			case Some(dbo) => toDish(dbo)
			case None => throw new NotFoundException("Dish")
		}
	}
	
	def getAll(userId: Long): List[Dish] = {
		coll.find(toUserId(userId)).map(toDish).toList
	}
	
	def toDish(dbo: DBObject): Dish = {
		val dish = grater[Dish].asObject(dbo)
		dish.copy(ingredients = toIngredients(dbo.getAs[List[BasicDBList]]("ingredients").get))
	}
	
	def toIngredients(ingredientsDbo: List[BasicDBList]): List[List[Ingredient]] = {
		ingredientsDbo.map((ingredientList: BasicDBList) => {
			ingredientList.map(ingredient => {
				val ingredientDbo = ingredient.asInstanceOf[BasicDBList]
				Ingredient(ingredientDbo.get(0).asInstanceOf[String], ingredientDbo.get(1).asInstanceOf[String])
			}).toList
		}).toList		
	}	
}