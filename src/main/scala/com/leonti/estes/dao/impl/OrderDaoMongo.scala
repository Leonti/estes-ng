package com.leonti.estes.dao.impl

import com.leonti.estes.dao.OrderDao
import com.leonti.estes.config.Configuration
import com.mongodb.casbah.MongoCollection
import com.leonti.estes.dao.NotFoundException
import com.leonti.estes.dao.DatabaseClient
import com.leonti.estes.domain.Order
import com.leonti.estes.domain.OrderArticle
import com.leonti.estes.domain.ArticleOption

import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._

class OrderDaoMongo extends OrderDao with Configuration with IdGeneratorMongo {
	val table = "order"
	lazy val coll: MongoCollection = DatabaseClient.db(table)
	
	def create(userId: Long, order: Order): Order = {
		val withId = order.copy(id = Some(generateId(table, userId)))
		coll.insert(grater[Order].asDBObject(withId))
		withId
	}
	
	def update(userId: Long, id: Long, order: Order): Order = {
		coll.update(toCompositeId(userId, id), grater[Order].asDBObject(order))
		order
	}
	
	def delete(userId: Long, id: Long) = {
		val order = get(userId, id)
		coll.remove(toCompositeId(userId, id))
		order
	}
	
	def get(userId: Long, id: Long): Order = {
		coll.findOne(toCompositeId(userId, id)) match {
			case Some(dbo) => toOrder(dbo)
			case None => throw new NotFoundException("Order")
		}
	}
	
	def getAll(userId: Long): List[Order] = {
		coll.find(toUserId(userId)).map(dbo => toOrder(dbo)).toList
	}
	
	def toOrder(dbo: DBObject): Order = {
		val order = grater[Order].asObject(dbo)
		order.copy(articles = toOrderArticles(dbo.getAs[List[BasicDBObject]]("articles").get))
	}
	
	def getInRange(id: Long, from: Long, to: Long): List[Order] = {
		val filter = MongoDBObject("submitted" -> MongoDBObject("$gte" -> from, "$lte" -> to))
		coll.find(filter).map(dbo => toOrder(dbo)).toList
	}
	
	def toOrderArticles(orderArticlesDbo: List[BasicDBObject]): List[OrderArticle] = {
		orderArticlesDbo.map(dbo => {
			toOrderArticle(dbo)
		}).toList
	}
	
	def toOrderArticle(dbo: DBObject): OrderArticle = {
		val article = grater[OrderArticle].asObject(dbo)
		article.copy(articleOptions = toOptions(dbo.getAs[List[BasicDBList]]("articleOptions").get))
	}
	
	def toOptions(optionsDbo: List[BasicDBList]): List[List[ArticleOption]] = {
		optionsDbo.map((optionList: BasicDBList) => {
			optionList.map(option => {
				val optionDbo = option.asInstanceOf[BasicDBList]
				ArticleOption(optionDbo.get(0).asInstanceOf[String], optionDbo.get(1).asInstanceOf[String], optionDbo.get(2).asInstanceOf[String])
			}).toList
		}).toList		
	}	
}