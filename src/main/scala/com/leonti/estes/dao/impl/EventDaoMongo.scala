package com.leonti.estes.dao.impl

import com.leonti.estes.domain.Event
import com.mongodb.casbah.MongoCollection
import com.leonti.estes.dao.NotFoundException
import com.leonti.estes.dao.EventDao
import com.leonti.estes.dao.DatabaseClient
import com.leonti.estes.config.Configuration

import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._

class EventDaoMongo extends EventDao with Configuration with IdGeneratorMongo {
	val table = "events"
	lazy val coll: MongoCollection = DatabaseClient.db(table)
	
	def create(userId: Long, event: Event): Event = {
		val withId = event.copy(id = Some(generateId(table, userId)))
		coll.insert(grater[Event].asDBObject(withId))
		withId
	}
	
	def update(userId: Long, id: Long, event: Event): Event = {
		coll.update(toCompositeId(userId, id), grater[Event].asDBObject(event))
		event
	}
	
	def delete(userId: Long, id: Long) = {
		val event = get(userId, id)
		coll.remove(toCompositeId(userId, id))
		event
	}
	
	def get(userId: Long, id: Long): Event = {
		coll.findOne(toCompositeId(userId, id)) match {
			case Some(dbo) => grater[Event].asObject(dbo)
			case None => throw new NotFoundException("Event")
		}
	}
	
	def getAll(userId: Long): List[Event] = {
		coll.find(toUserId(userId)).map(dbo => grater[Event].asObject(dbo)).toList
	}	
}