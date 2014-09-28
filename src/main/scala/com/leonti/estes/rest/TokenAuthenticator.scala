package com.leonti.estes.rest

import scala.Left
import scala.Right
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import spray.routing.AuthenticationFailedRejection
import spray.routing.AuthenticationFailedRejection.CredentialsMissing
import spray.routing.AuthenticationFailedRejection.CredentialsRejected
import spray.routing.RequestContext
import spray.routing.authentication.Authentication
import spray.routing.authentication.ContextAuthenticator
import scala.util.matching.Regex

/**
 * Token based authentication for Spray Routing.
 *
 * Extracts an API key from the header or querystring and authenticates requests.
 *
 * TokenAuthenticator[T] takes arguments for the named header/query string containing the API key and
 * an authenticator that returns an Option[T]. If None is returned from the authenticator, the request
 * is rejected.
 *
 * Usage:
 *
 * val authenticator = TokenAuthenticator[User](
 * headerName = "My-Api-Key",
 * queryStringParameterName = "api_key"
 * ) { key =>
 * User.findByAPIKey(key)
 * }
 *
 * def auth: Directive1[User] = authenticate(authenticator)
 *
 * val home = path("home") {
 * auth { user =>
 * get {
 * complete("OK")
 * }
 * }
 * }
 */

object TokenAuthenticator {

	object TokenExtraction {

		type TokenExtractor = RequestContext => Option[(Long, String)]

		val pattern = new Regex(".*/user/([0-9]+)/.*")

		def parseUserId(uri: String): Option[Long] = pattern findFirstIn uri match {
			case Some(pattern(userId)) => Some(userId.toLong)
			case _ => None
		}

		def fromHeader(headerName: String): TokenExtractor = { context: RequestContext =>
			for {
				userId <- parseUserId(context.request.uri.path.toString)
				token <- context.request.headers.find(_.name == headerName).map(_.value)
			} yield (userId, token)
		}

		def fromQueryString(parameterName: String): TokenExtractor = { context: RequestContext =>
			for {
				userId <- parseUserId(context.request.uri.path.toString)
				token <- context.request.uri.query.get(parameterName)
			} yield (userId, token)
		}

	}

	class TokenAuthenticator[T](extractor: TokenExtraction.TokenExtractor, authenticator: ((Long, String) => Future[Option[T]]))(implicit executionContext: ExecutionContext) extends ContextAuthenticator[T] {

		import AuthenticationFailedRejection._

		def apply(context: RequestContext): Future[Authentication[T]] =
			extractor(context) match {
				case None =>
					Future(
						Left(AuthenticationFailedRejection(CredentialsMissing, List())))
				case Some((userId, token)) =>
					authenticator(userId, token) map {
						case Some(t) =>
							Right(t)
						case None =>
							Left(AuthenticationFailedRejection(CredentialsRejected, List()))
					}
			}

	}

	def apply[T](headerName: String, queryStringParameterName: String)(authenticator: ((Long, String) => Future[Option[T]]))(implicit executionContext: ExecutionContext) = {

		def extractor(context: RequestContext) =
			TokenExtraction.fromHeader(headerName)(context) orElse
				TokenExtraction.fromQueryString(queryStringParameterName)(context)

		new TokenAuthenticator(extractor, authenticator)
	}

}