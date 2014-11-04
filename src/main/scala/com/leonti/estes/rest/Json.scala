package com.leonti.estes.rest

import spray.json.DefaultJsonProtocol
import com.leonti.estes.domain._

object JsonProtocol extends DefaultJsonProtocol {
	implicit val compositeIdFormat = jsonFormat2(CompositeId)
	implicit val waiterFormat = jsonFormat2(Waiter)
  implicit val articleOptionFormat = jsonFormat3(ArticleOption)
  implicit val taxGroupFormat = jsonFormat3(TaxGroup)
  implicit val articleFormat = jsonFormat7(Article)
  implicit val orderArticleFormat = jsonFormat9(OrderArticle)
  implicit val orderFormat = jsonFormat8(Order)
  implicit val orderPreparedFormat = jsonFormat1(OrderPrepared)
  implicit val articlePreparedFormat = jsonFormat2(ArticlePrepared)
  implicit val eventFormat = jsonFormat5(Event)
  implicit val userFormat = jsonFormat2(User)
  implicit val userSessionFormat = jsonFormat3(UserSession)
  implicit val settingsFormat = jsonFormat3(Settings)
  implicit val oauthErrorFormat = jsonFormat2(OauthError)
  implicit val oauthResponseFormat = jsonFormat4(OauthResponse)
  implicit val patchRequestFormat = jsonFormat3(PatchRequest)
}