package com.leonti.estes.domain

import com.novus.salat.annotations._
import spray.json.DefaultJsonProtocol
import spray.json._
import DefaultJsonProtocol._

case class Waiter(@Key("_id") id: Option[Long], name: String)
case class Ingredient(@Key("_id") id: Option[Long], name: String, priceChange: BigDecimal)
case class Dish(@Key("_id") id: Option[Long], name: String, ingredients: List[Ingredient], price: BigDecimal)
case class Order(@Key("_id") id: Option[Long], waiter: Waiter, dishes: List[Dish])

object JsonProtocol extends DefaultJsonProtocol {
	implicit val waiterFormat = jsonFormat2(Waiter)
  implicit val ingredientFormat = jsonFormat3(Ingredient)
  implicit val dishFormat = jsonFormat4(Dish)
  implicit val orderFormat = jsonFormat3(Order)
}