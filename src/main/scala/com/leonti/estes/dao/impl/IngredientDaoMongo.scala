package com.leonti.estes.dao.impl

import com.leonti.estes.config.Configuration
import com.leonti.estes.dao.DatabaseClient
import com.leonti.estes.dao.IngredientDao
import com.leonti.estes.dao.NotFoundException
import com.leonti.estes.domain.Ingredient
import com.mongodb.casbah.Imports.unwrapDBObj
import com.mongodb.casbah.Imports.wrapDBObj
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._

class IngredientDaoMongo extends IngredientDao with Configuration with IdGeneratorMongo {
	val table = "ingredient"
	lazy val coll: MongoCollection = DatabaseClient.db(table)
	
	def create(userId: Long, ingredient: Ingredient): Ingredient = {
		val withId = ingredient.copy(id = Some(generateId(table, userId)))
		coll.insert(grater[Ingredient].asDBObject(withId))
		withId
	}
	
	def update(userId: Long, id: Long, ingredient: Ingredient): Ingredient = {
		coll.update(toCompositeId(userId, id), grater[Ingredient].asDBObject(ingredient))
		ingredient
	}
	
	def delete(userId: Long, id: Long) = {
		val ingredient = get(userId, id)
		coll.remove(toCompositeId(userId, id))
		ingredient
	}
	
	def get(userId: Long, id: Long): Ingredient = {
		coll.findOne(toCompositeId(userId, id)) match {
			case Some(dbo) => grater[Ingredient].asObject(dbo)
			case None => throw new NotFoundException("Ingredient")
		}
	}
	
	def getAll(userId: Long): List[Ingredient] = {
		coll.find(toUserId(userId)).map(dbo => grater[Ingredient].asObject(dbo)).toList
	}	
}