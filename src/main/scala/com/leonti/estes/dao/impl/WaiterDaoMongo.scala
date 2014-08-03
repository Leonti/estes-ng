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
	
	def create(waiter: Waiter): Waiter = {
		val withId = waiter.copy(id = Some(generateId(table)))
		coll.insert(grater[Waiter].asDBObject(withId))
		withId
	}
	
	def update(id: Long, waiter: Waiter): Waiter = {
		coll.update(toId(id), grater[Waiter].asDBObject(waiter))
		waiter
	}
	
	def delete(id: Long) = {
		val waiter = get(id)
		coll.remove(toId(id))
		waiter
	}
	
	def get(id: Long): Waiter = {
		coll.findOne(toId(id)) match {
			case Some(dbo) => grater[Waiter].asObject(dbo)
			case None => throw new NotFoundException("Waiter")
		}
	}
	
	def getAll(): List[Waiter] = {
		coll.map(dbo => grater[Waiter].asObject(dbo)).toList
	}	
}