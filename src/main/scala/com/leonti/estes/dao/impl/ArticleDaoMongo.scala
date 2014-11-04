package com.leonti.estes.dao.impl

import com.leonti.estes.config.Configuration
import com.leonti.estes.dao.Dao
import com.leonti.estes.dao.DatabaseClient
import com.leonti.estes.dao.ArticleDao
import com.leonti.estes.dao.NotFoundException
import com.leonti.estes.domain.ArticleOption
import com.leonti.estes.domain.Article
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._
import scala.collection.JavaConversions._

class ArticleDaoMongo extends ArticleDao with Configuration with IdGeneratorMongo {
	val table = "articles"
	lazy val coll: MongoCollection = DatabaseClient.db(table)
	
	def create(userId: Long, article: Article): Article = {
		val withId = article.copy(id = Some(generateId(table, userId)))		
		coll.insert(grater[Article].asDBObject(withId))
		withId
	}
	
	def update(userId: Long, id: Long, article: Article): Article = {
		coll.update(toCompositeId(userId, id), grater[Article].asDBObject(article))
		article
	}
	
	def delete(userId: Long, id: Long) = {
		val article = get(userId, id)
		coll.remove(toCompositeId(userId, id))
		article
	}
	
	def get(userId: Long, id: Long): Article = {
		coll.findOne(toCompositeId(userId, id)) match {
			case Some(dbo) => toArticle(dbo)
			case None => throw new NotFoundException("Article")
		}
	}
	
	def getAll(userId: Long): List[Article] = {
		coll.find(toUserId(userId)).map(toArticle).toList
	}
	
	def toArticle(dbo: DBObject): Article = {
		val article = grater[Article].asObject(dbo)
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