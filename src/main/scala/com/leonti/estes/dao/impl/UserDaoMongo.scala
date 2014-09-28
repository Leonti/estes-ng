package com.leonti.estes.dao.impl

import com.leonti.estes.dao.UserDao
import com.leonti.estes.config.Configuration
import com.mongodb.casbah.MongoCollection
import com.leonti.estes.dao.NotFoundException
import com.leonti.estes.dao.DatabaseClient
import com.leonti.estes.domain.User

import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._

class UserDaoMongo extends UserDao with Configuration with IdGeneratorMongo {
	val table = "user"
	lazy val coll: MongoCollection = DatabaseClient.db(table)
	
	def create(user: User): User = {
		val withId = user.copy(id = Some(generateId(table)))
		coll.insert(grater[User].asDBObject(withId))
		withId
	}
	
	def update(id: Long, user: User): User = {
		coll.update(toId(id), grater[User].asDBObject(user))
		user
	}
	
	def delete(id: Long) = {
		val order = get(id)
		coll.remove(toId(id))
		order
	}
	
	def getByEmail(email: String): Option[User] = {
		for {
			dbo <- coll.findOne(MongoDBObject("email" -> email))
		} yield grater[User].asObject(dbo)
	}
	
	def get(id: Long): User = {
		coll.findOne(toId(id)) match {
			case Some(dbo) => grater[User].asObject(dbo)
			case None => throw new NotFoundException("User")
		}
	}
	
	def getAll(): List[User] = {
		coll.map(dbo => grater[User].asObject(dbo)).toList
	}	
}