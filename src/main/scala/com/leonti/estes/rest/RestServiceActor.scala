package com.leonti.estes.rest

import scala.concurrent.Future
import com.leonti.estes.domain._
import com.leonti.estes.domain.JsonProtocol._
import com.leonti.estes.service._
import akka.actor.Actor
import akka.event.slf4j.SLF4JLogging
import spray.http._
import spray.client.pipelining._
import spray.httpx.SprayJsonSupport._
import spray.httpx.unmarshalling._
import spray.json._
import spray.routing._
import spray.routing.HttpService
import spray.routing.authentication.BasicAuth
import spray.routing.authentication.UserPass
import scala.util.Success
import scala.util.Failure
import scala.collection.immutable.HashMap

class RestServiceActor extends Actor with RestService {

	implicit def actorRefFactory = context

	def receive = runRoute(restRoute)

}

case class User(id: Long, name: String)

trait RestService extends HttpService with CORSSupport with TokenAuthentication with SLF4JLogging {

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
			authenticate(tokenAuthenticator) { user =>
				pathPrefix("rest") {
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
									println(s"username is '$user'")
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
			} ~
				path("login") {
					ctx => {
						println("No authentication!")
						
						val pipeline: HttpRequest => Future[Map[String, String]] = (sendReceive ~> unmarshal[Map[String, String]])
						val response: Future[Map[String, String]] = pipeline(Get("https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=ya29.egA8k9VcfSa1NR0AAAC9LDx_ofMf2RWmO2UCnKkbM0FQA0gAdmPl1gKcZ18aLg"))
						
						response onComplete {
							case Success(content: Map[String, String]) => ctx.complete(content)
							case Failure(t) => ctx.complete(t.getMessage)
						}
						
					}
				}

		}
	}

}