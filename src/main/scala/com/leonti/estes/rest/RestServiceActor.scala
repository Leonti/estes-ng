package com.leonti.estes.rest

import scala.concurrent.Future
import com.leonti.estes.domain._
import com.leonti.estes.rest.JsonProtocol._
import com.leonti.estes.service._
import akka.actor.Actor
import akka.event.slf4j.SLF4JLogging
import spray.client.pipelining._
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.httpx.unmarshalling._
import spray.json._
import spray.routing._
import spray.routing.HttpService
import scala.util.Success
import scala.util.Failure

class RestServiceActor extends Actor with RestService {

	implicit def actorRefFactory = context

	def receive = runRoute(restRoute)

}

//object OauthJsonProtocol extends DefaultJsonProtocol {
//	
//}

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
				pathPrefix("rest" / "user" / LongNumber) {
					userId =>
						{
							path("article") {
								post {
									entity(as[Article]) {
										article =>
											complete {
												ArticleService.create(userId, article)
											}
									}
								} ~
									get {
										complete {
											ArticleService.getAll(userId)
										}
									}
							} ~
								path("article" / LongNumber) {
									articleId =>
										{
											get {
												complete {
													ArticleService.get(userId, articleId)
												}
											} ~
												put {
													entity(as[Article]) {
														article =>
															complete {
																ArticleService.update(userId, articleId, article)
															}
													}
												} ~
												delete {
													complete {
														ArticleService.delete(userId, articleId)
													}
												}
										}
								} ~
								path("waiter") {
									post {
										entity(as[Waiter]) {
											waiter =>
												complete {
													WaiterService.create(userId, waiter)
												}
										}
									} ~
										get {
											complete {
												WaiterService.getAll(userId)
											}
										}
								} ~
								path("waiter" / LongNumber) {
									waiterId =>
										{
											get {
												complete {
													WaiterService.get(userId, waiterId)
												}
											} ~
												put {
													entity(as[Waiter]) {
														waiter =>
															complete {
																WaiterService.update(userId, waiterId, waiter)
															}
													}
												} ~
												delete {
													complete {
														WaiterService.delete(userId, waiterId)
													}
												}
										}
								} ~
								path("order") {
									post {
										entity(as[Order]) {
											order =>
												complete {
													OrderService.createOrder(userId, order)
												}
										}
									} ~
									get {
										complete {
											OrderService.getAll(userId)
										}
									}
								} ~
								path("order" / LongNumber) {
									orderId =>
										{
											get {
												complete {
													OrderService.get(userId, orderId)
												}
											} ~
												put {
													entity(as[Order]) {
														order =>
															complete {
																OrderService.update(userId, orderId, order)
															}
													}
												} ~
												delete {
													complete {
														OrderService.delete(userId, orderId)
													}
												} ~
												patch {
													entity(as[PatchRequest]) {
														patchRequest =>
															complete {
																OrderService.patchOrder(userId, orderId, patchRequest)
															}
													}
												}
										}
								} ~
								path("order" / "from" / LongNumber / "to" / LongNumber) {
									(from, to) => {
										get {
											complete {
												OrderService.getInRange(userId, from, to)
											}
										}										
									}
								} ~
								path("settings") {
									get {
										complete {
											SettingsService.getOrCreate(userId)
										}
									} ~
										put {
											entity(as[Settings]) {
												settings =>
													{
														complete {
															SettingsService.update(userId, settings)
														}
													}
											}
										}
								} ~
								path("event") {
									get {
										complete {
											EventService.getAll(userId)
										}
									}
								} ~
								path("event" / LongNumber) {
									eventId =>
										{
											patch {
												entity(as[PatchRequest]) {
													patchRequest =>
														complete {
															EventService.patchEvent(userId, eventId, patchRequest)
														}
												}
											}
										}
								}
						}
				}
			} ~
				path("login" / Segment) {
					token =>
						{
							ctx =>
								{

									val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
									val response: Future[HttpResponse] = pipeline(Get(s"https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=$token"))

									response onComplete {
										case Success(httpResponse: HttpResponse) => {
											if (httpResponse.status.isSuccess) {
												val oauthResponse = JsonParser(httpResponse.entity.asString).convertTo[OauthResponse];
												val user = UserService.getOrCreate(oauthResponse.email)
												val userSession = UserSessionService.createUserSession(user)

												println(s"Logged in user '$oauthResponse'")

												ctx.complete(userSession)
											} else {
												ctx.complete(JsonParser(httpResponse.entity.asString).convertTo[OauthError])
											}
										}
										case Failure(t) => {
											ctx.complete(t.getMessage)
										}
									}
								}
						}
				} ~
				path("logout" / Segment) {
					token =>
						{
							complete {
								UserSessionService.delete(token)
								"ok"
							}
						}
				}

		}
	}

}