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
	
	def create(ingredient: Ingredient): Ingredient = {
		val withId = ingredient.copy(id = Some(generateId(table)))
		coll.insert(grater[Ingredient].asDBObject(withId))
		withId
	}
	
	def update(id: Long, ingredient: Ingredient): Ingredient = {
		coll.update(toId(id), grater[Ingredient].asDBObject(ingredient))
		ingredient
	}
	
	def delete(id: Long) = {
		val ingredient = get(id)
		coll.remove(toId(id))
		ingredient
	}
	
	def get(id: Long): Ingredient = {
		coll.findOne(toId(id)) match {
			case Some(dbo) => grater[Ingredient].asObject(dbo)
			case None => throw new NotFoundException("Ingredient")
		}
	}
	
	def getAll(): List[Ingredient] = {
		coll.map(dbo => grater[Ingredient].asObject(dbo)).toList
	}	
}