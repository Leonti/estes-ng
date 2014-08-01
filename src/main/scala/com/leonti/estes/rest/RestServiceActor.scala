package com.leonti.estes.rest

import spray.json._
import _root_.com.leonti.estes.domain.JsonProtocol._
import _root_.com.leonti.estes.domain._
import _root_.com.leonti.estes.service._
import akka.actor.Actor
import spray.routing.HttpService
import akka.event.slf4j.SLF4JLogging
import spray.http._
import spray.httpx.unmarshalling._
import spray.httpx.SprayJsonSupport._
import spray.routing._
import scala.util.Try

class RestServiceActor extends Actor with RestService {

	implicit def actorRefFactory = context

	def receive = runRoute(restRoute)

}

trait RestService extends HttpService with SLF4JLogging {

	implicit val executionContext = actorRefFactory.dispatcher

	implicit val customRejectionHandler = RejectionHandler {
		case rejections => mapHttpResponse {
			response =>
				response.withEntity(HttpEntity(ContentType(MediaTypes.`application/json`),
					Map("error" -> response.entity.asString).toJson.prettyPrint))
		} {
			RejectionHandler.Default(rejections)
		}
	}

	val restRoute = respondWithMediaType(MediaTypes.`application/json`) {

		path("dish") {
			post {
				entity(as[Dish]) {
					dish =>
						complete {
							DishService.create(dish).toJson.prettyPrint
						}
				}

			}
		} ~
		path("dish" / LongNumber) {
			dishId =>
				{
					get {
						complete {
							DishService.get(dishId).toJson.prettyPrint
						}
					} ~
					put {
						entity(as[Dish]) {
							dish =>
								complete {
									DishService.update(dishId, dish).toJson.prettyPrint
								}
						}
					} ~
					delete {
						complete {
							DishService.delete(dishId)
						}
					}
				}
		} ~
		path("ingredient") {
			post {
				entity(as[Ingredient]) {
					ingredient =>
						complete {
							IngredientService.create(ingredient).toJson.prettyPrint
						}
				}

			}
		} ~
		path("ingredient" / LongNumber) {
			ingredientId =>
				{
					get {
						complete {
							IngredientService.get(ingredientId).toJson.prettyPrint
						}
					} ~
					put {
						entity(as[Ingredient]) {
							ingredient =>
								complete {
									IngredientService.update(ingredientId, ingredient).toJson.prettyPrint
								}
						}
					} ~
					delete {
						complete {
							IngredientService.delete(ingredientId)
						}
					}
				}
		} ~		
		path("waiter") {
			post {
				entity(as[Waiter]) {
					waiter =>
						complete {
							WaiterService.create(waiter).toJson.prettyPrint
						}
				}

			}
		} ~
		path("waiter" / LongNumber) {
			waiterId =>
				{
					get {
						complete {
							WaiterService.get(waiterId).toJson.prettyPrint
						}
					} ~
					put {
						entity(as[Waiter]) {
							waiter =>
								complete {
									WaiterService.update(waiterId, waiter).toJson.prettyPrint
								}
						}
					} ~
					delete {
						complete {
							WaiterService.delete(waiterId)
						}
					}
				}
		} ~
		path("order") {
			post {
				entity(as[Order]) {
					order =>
						complete {
							OrderService.create(order).toJson.prettyPrint
						}
				}

			}
		} ~
		path("order" / LongNumber) {
			orderId =>
				{
					get {
						complete {
							OrderService.get(orderId).toJson.prettyPrint
						}
					} ~
					put {
						entity(as[Order]) {
							order =>
								complete {
									OrderService.update(orderId, order).toJson.prettyPrint
								}
						}
					} ~
					delete {
						complete {
							OrderService.delete(orderId)
						}
					}
				}
		}		
	}
	
}