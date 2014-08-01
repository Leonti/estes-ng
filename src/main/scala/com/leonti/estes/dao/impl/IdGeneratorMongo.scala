package com.leonti.estes.dao.impl

import com.leonti.estes.dao.DatabaseClient
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
trait IdGeneratorMongo {
	def generateId(table: String): Long = {
		val coll = DatabaseClient.db("counters")
		val id = MongoDBObject("_id" -> table)
		coll.findOne(id) match {
			case None => coll.insert(id.++("counter" -> 1L))
			case _ =>
		}
		val result = coll.findAndModify(id, $inc("counter" -> 1))
		result.get.as[Long]("counter")
	}
	
	def toId(id: Long): MongoDBObject = MongoDBObject("_id" -> id) 
	
}