package com.leonti.estes.rest

import spray.json.DefaultJsonProtocol
import com.leonti.estes.domain._

object JsonProtocol extends DefaultJsonProtocol {
	implicit val compositeIdFromat = jsonFormat2(CompositeId)
	implicit val waiterFormat = jsonFormat2(Waiter)
  implicit val ingredientFormat = jsonFormat2(Ingredient)
  implicit val dishFormat = jsonFormat6(Dish)
  implicit val orderFormat = jsonFormat6(Order)
  implicit val userFormat = jsonFormat2(User)
  implicit val userSessionFormat = jsonFormat3(UserSession)
  implicit val settingsFormat = jsonFormat4(Settings)
  implicit val oauthErrorFormat = jsonFormat2(OauthError)
  implicit val oauthResponseFormat = jsonFormat4(OauthResponse)
}