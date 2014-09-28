package com.leonti.estes.dao.impl

import com.leonti.estes.dao.OrderDao
import com.leonti.estes.config.Configuration
import com.mongodb.casbah.MongoCollection
import com.leonti.estes.dao.NotFoundException
import com.leonti.estes.dao.DatabaseClient
import com.leonti.estes.domain.Order
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._

class OrderDaoMongo extends OrderDao with Configuration with IdGeneratorMongo {
	val table = "order"
	lazy val coll: MongoCollection = DatabaseClient.db(table)
	
	def create(order: Order): Order = {
		val withId = order.copy(id = generateId(table, order.id.userId))
		coll.insert(grater[Order].asDBObject(withId))
		withId
	}
	
	def update(userId: Long, id: Long, order: Order): Order = {
		coll.update(toCompositeId(userId, id), grater[Order].asDBObject(order))
		order
	}
	
	def delete(userId: Long, id: Long) = {
		val order = get(userId, id)
		coll.remove(toCompositeId(userId, id))
		order
	}
	
	def get(userId: Long, id: Long): Order = {
		coll.findOne(toCompositeId(userId, id)) match {
			case Some(dbo) => grater[Order].asObject(dbo)
			case None => throw new NotFoundException("Order")
		}
	}
	
	def getAll(userId: Long): List[Order] = {
		coll.find(toUserId(userId)).map(dbo => grater[Order].asObject(dbo)).toList
	}	
}