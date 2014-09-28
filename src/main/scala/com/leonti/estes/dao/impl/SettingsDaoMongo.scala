package com.leonti.estes.dao.impl

import com.leonti.estes.domain.Settings
import com.leonti.estes.dao.SettingsDao
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._
import com.leonti.estes.dao.DatabaseClient
import com.leonti.estes.dao.NotFoundException

class SettingsDaoMongo extends SettingsDao {
	val table = "settings"
	lazy val coll: MongoCollection = DatabaseClient.db(table)
	
	def create(settings: Settings): Settings = {
		coll.insert(grater[Settings].asDBObject(settings))
		settings
	}
	
	def update(userId: Long, settings: Settings): Settings = {
		coll.update(toId(userId), grater[Settings].asDBObject(settings))
		settings		
	}
	
	def delete(userId: Long): Unit = {
		coll.remove(toId(userId))
	}
	
	def get(userId: Long): Settings = {
		tryToGet(userId) match {
			case Some(settings) => settings
			case None => throw new NotFoundException("Settings")
		}				
	}
	
	def tryToGet(userId: Long): Option[Settings] = {
		for {
			dbo <- coll.findOne(toId(userId))
		} yield grater[Settings].asObject(dbo)			
	}	
	
	def toId(userId: Long): MongoDBObject = {
		MongoDBObject("_id" -> userId)
	}	
}