package com.leonti.estes.dao.impl

import com.leonti.estes.dao.DatabaseClient
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat._
import com.novus.salat.global._
import com.leonti.estes.domain.CompositeId

trait IdGeneratorMongo {
	def generateId(table: String, userId: Long): CompositeId = {
		val coll = DatabaseClient.db("counters")
		val id = MongoDBObject("_id" -> MongoDBObject("userId" -> userId, "table" -> table))
		coll.findOne(id) match {
			case None => coll.insert(id.++("counter" -> 1L))
			case _ =>
		}
		val result = coll.findAndModify(id, $inc("counter" -> 1))
		result.get.as[Long]("counter")
		CompositeId(userId = userId, id = result.get.as[Long]("counter"))
	}
	
	def generateId(table: String): Long = {
		generateId(table, 0).id
	}
	
	def toCompositeId(userId: Long, id: Long): MongoDBObject = MongoDBObject("_id" -> grater[CompositeId].asDBObject(CompositeId(userId, id))) 
	def toId(id: Long): MongoDBObject = MongoDBObject("_id" -> id)
	def toUserId(userId: Long) : MongoDBObject = MongoDBObject("_id.userId" -> userId)
}