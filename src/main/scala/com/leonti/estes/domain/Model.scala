package com.leonti.estes.domain

import com.novus.salat.annotations._
import spray.json.DefaultJsonProtocol
import spray.json._
import DefaultJsonProtocol._

case class CompositeId(userId: Long, id: Long)

case class User(@Key("_id") id: Option[Long], email: String)
case class UserSession(@Key("_id") id: String, userId: Long, created: Long)

case class Settings(@Key("_id") userId: Long, printer: String, receiptWidth: Int)
case class Waiter(@Key("_id") id: Option[CompositeId], name: String)
case class ArticleOption(id: String, name: String, priceChange: String)
case class TaxGroup(id: String, name: String, tax: String)

case class Article(@Key("_id") id: Option[CompositeId], name: String, price: String, tags: List[String], 
		articleOptions: List[List[ArticleOption]], taxGroup: TaxGroup, kitchen: Boolean)

case class OrderArticle(id: String, name: String, price: String, tax: String, discount: String, 
		articleOptions: List[List[ArticleOption]], selectedOptions: List[ArticleOption], status: String, kitchen: Boolean)

case class Order(@Key("_id") id: Option[CompositeId], dayId: Option[Int], waiter: Waiter, submitted: Option[Long], 
		articles: List[OrderArticle], discount: String, status: String,  note: String)

case class OrderPrepared(orderId: CompositeId)		
case class ArticlePrepared(orderId: CompositeId, articleId: String)		
case class Event(@Key("_id") id: Option[CompositeId], timestamp: Option[Long], ack: List[String],
		orderPrepared: OrderPrepared, articlePrepared: ArticlePrepared)

case class PatchRequest(op: String, path: String, value: String)		
		
case class OauthError(error: String, error_description: String)
case class OauthResponse(user_id: String, scope: String, email: String, verified_email: Boolean)

