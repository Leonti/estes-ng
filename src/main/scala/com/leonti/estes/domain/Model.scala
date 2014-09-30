package com.leonti.estes.domain

import com.novus.salat.annotations._
import spray.json.DefaultJsonProtocol
import spray.json._
import DefaultJsonProtocol._

case class CompositeId(userId: Long, id: Long)

case class User(@Key("_id") id: Option[Long], email: String)
case class UserSession(@Key("_id") id: String, userId: Long, created: Long)
case class Settings(@Key("_id") userId: Long, tax: BigDecimal, printer: String, receiptWidth: Int)
case class Waiter(@Key("_id") id: Option[CompositeId], name: String)
case class Ingredient(@Key("_id") id: Option[CompositeId], name: String, priceChange: BigDecimal)
case class Dish(@Key("_id") id: Option[CompositeId], name: String, ingredients: List[Ingredient], selectedIngredients: List[Ingredient], menus: List[String], price: BigDecimal)
case class Order(@Key("_id") id: Option[CompositeId], waiter: Waiter, dishes: List[Dish], submitted: Long, status: String, note: String)

case class OauthError(error: String, error_description: String)
case class OauthResponse(user_id: String, scope: String, email: String, verified_email: Boolean)

