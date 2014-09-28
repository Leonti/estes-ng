package com.leonti.estes.rest

import spray.json.DefaultJsonProtocol
import com.leonti.estes.domain._

object JsonProtocol extends DefaultJsonProtocol {
	implicit val compositeIdFromat = jsonFormat2(CompositeId)
	implicit val waiterFormat = jsonFormat2(Waiter)
  implicit val ingredientFormat = jsonFormat3(Ingredient)
  implicit val dishFormat = jsonFormat4(Dish)
  implicit val orderFormat = jsonFormat3(Order)
  implicit val userFormat = jsonFormat2(User)
  implicit val userSessionFormat = jsonFormat3(UserSession)
  implicit val oauthErrorFormat = jsonFormat2(OauthError)
  implicit val oauthResponseFormat = jsonFormat4(OauthResponse)
}