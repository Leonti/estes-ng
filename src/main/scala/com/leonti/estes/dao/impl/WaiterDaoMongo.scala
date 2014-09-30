package com.leonti.estes.dao.impl

import com.leonti.estes.dao.WaiterDao
import com.leonti.estes.config.Configuration
import com.mongodb.casbah.MongoCollection
import com.leonti.estes.dao.NotFoundException
import com.leonti.estes.dao.DatabaseClient
import com.leonti.estes.domain.Waiter

import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._

class WaiterDaoMongo extends WaiterDao with Configuration with IdGeneratorMongo {
	val table = "waiter"
	lazy val coll: MongoCollection = DatabaseClient.db(table)
	
	def create(userId: Long, waiter: Waiter): Waiter = {
		val withId = waiter.copy(id = Some(generateId(table, userId)))
		coll.insert(grater[Waiter].asDBObject(withId))
		withId
	}
	
	def update(userId: Long, id: Long, waiter: Waiter): Waiter = {
		coll.update(toCompositeId(userId, id), grater[Waiter].asDBObject(waiter))
		waiter
	}
	
	def delete(userId: Long, id: Long) = {
		val waiter = get(userId, id)
		coll.remove(toCompositeId(userId, id))
		waiter
	}
	
	def get(userId: Long, id: Long): Waiter = {
		coll.findOne(toCompositeId(userId, id)) match {
			case Some(dbo) => grater[Waiter].asObject(dbo)
			case None => throw new NotFoundException("Waiter")
		}
	}
	
	def getAll(userId: Long): List[Waiter] = {
		coll.find(toUserId(userId)).map(dbo => grater[Waiter].asObject(dbo)).toList
	}	
}