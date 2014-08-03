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
		val withId = order.copy(id = Some(generateId(table)))
		coll.insert(grater[Order].asDBObject(withId))
		withId
	}
	
	def update(id: Long, order: Order): Order = {
		coll.update(toId(id), grater[Order].asDBObject(order))
		order
	}
	
	def delete(id: Long) = {
		val order = get(id)
		coll.remove(toId(id))
		order
	}
	
	def get(id: Long): Order = {
		coll.findOne(toId(id)) match {
			case Some(dbo) => grater[Order].asObject(dbo)
			case None => throw new NotFoundException("Order")
		}
	}
	
	def getAll(): List[Order] = {
		coll.map(dbo => grater[Order].asObject(dbo)).toList
	}	
}