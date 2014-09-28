package com.leonti.estes.rest

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import com.leonti.estes.domain._

trait TokenAuthentication {
	
	val tokenAuthenticator = TokenAuthenticator[User] (headerName = "My-Api-Key", queryStringParameterName = "api_key") { (userId, token) =>
			Future {
				
				println(s"Authenticated user with id '$userId' and token '$token'")
				
				Some(User(Some(userId), "prishelec@gmail.com"))
			}
		}		

}