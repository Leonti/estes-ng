package com.leonti.estes.config

import com.typesafe.config.ConfigFactory
import scala.util.Try

trait Configuration {

	val config = ConfigFactory.load()
	
  lazy val serviceHost = config.getString("service.host")
  lazy val servicePort = config.getInt("service.port")
  lazy val dbHost = config.getString("db.host")
  lazy val dbPort = config.getInt("db.port")
  lazy val dbName = config.getString("db.name")
  lazy val dbUser = config.getString("db.user")
  lazy val dbPassword = config.getString("db.password")
	
}