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

trait RestService extends HttpService with CORSSupport with SLF4JLogging {

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

	val restRoute: Route = respondWithMediaType(MediaTypes.`application/json`) {
		cors {
			path("dish") {
				post {
					entity(as[Dish]) {
						dish =>
							complete {
								DishService.create(dish)
							}
					}
				} ~
				get {
					complete {
						DishService.getAll()
					}
				}
			} ~
			path("dish" / LongNumber) {
				dishId =>
					{
						get {
							complete {
								DishService.get(dishId)
							}
						} ~
						put {
							entity(as[Dish]) {
								dish =>
									complete {
										DishService.update(dishId, dish)
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
								IngredientService.create(ingredient)
							}
					}
				} ~
				get {
					complete {
						IngredientService.getAll()
					}
				}
			} ~
			path("ingredient" / LongNumber) {
				ingredientId =>
					{
						get {
							complete {
								IngredientService.get(ingredientId)
							}
						} ~
						put {
							entity(as[Ingredient]) {
								ingredient =>
									complete {
										IngredientService.update(ingredientId, ingredient)
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
								WaiterService.create(waiter)
							}
					}
				} ~
				get {
					complete {
						WaiterService.getAll()
					}
				}
			} ~
			path("waiter" / LongNumber) {
				waiterId =>
					{
						get {
							complete {
								WaiterService.get(waiterId)
							}
						} ~
						put {
							entity(as[Waiter]) {
								waiter =>
									complete {
										WaiterService.update(waiterId, waiter)
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
								OrderService.create(order)
							}
					}
				} ~
				get {
					complete {
						OrderService.getAll()
					}
				}
			} ~
			path("order" / LongNumber) {
				orderId =>
					{
						get {
							complete {
								OrderService.get(orderId)
							}
						} ~
						put {
							entity(as[Order]) {
								order =>
									complete {
										OrderService.update(orderId, order)
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
	
}