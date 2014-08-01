package com.leonti.estes.dao

import com.leonti.estes.config.Configuration
import com.mongodb.casbah.MongoClient

object DatabaseClient extends Configuration {

	lazy val db = {
		val mongoClient =  MongoClient(dbHost, dbPort)
		mongoClient(dbName)
	}
}