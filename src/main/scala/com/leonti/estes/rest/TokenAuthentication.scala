package com.leonti.estes.rest

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait TokenAuthentication {
	
	val tokenAuthenticator = TokenAuthenticator[User](
		headerName = "My-Api-Key",
		queryStringParameterName = "api_key") { key =>
			Future {
				Some(User(10, "John-token " + key))
			}
		}		

}