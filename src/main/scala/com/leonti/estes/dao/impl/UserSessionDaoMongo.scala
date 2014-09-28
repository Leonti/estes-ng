package com.leonti.estes.dao.impl

import com.leonti.estes.config.Configuration
import com.leonti.estes.dao.DatabaseClient
import com.leonti.estes.dao.UserSessionDao
import com.leonti.estes.domain.UserSession
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoCollection
import com.novus.salat._
import com.novus.salat.global._
import com.leonti.estes.dao.NotFoundException

class UserSessionDaoMongo extends UserSessionDao with Configuration {
	val table = "userSession"
	lazy val coll: MongoCollection = DatabaseClient.db(table)
	
	def create(userSession: UserSession): UserSession = {
		coll.insert(grater[UserSession].asDBObject(userSession))
		userSession
	} 
	
	def delete(id: String) = {
		coll.remove(toId(id))
	} 
	
	def get(id: String): UserSession = {
		coll.findOne(toId(id)) match {
			case Some(dbo) => grater[UserSession].asObject(dbo)
			case None => throw new NotFoundException("UserSession")
		}		
	}
	
	def toId(id: String): MongoDBObject = {
		MongoDBObject("_id" -> id)
	}
}