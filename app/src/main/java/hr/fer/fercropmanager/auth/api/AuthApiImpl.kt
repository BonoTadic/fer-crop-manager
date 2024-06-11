package hr.fer.fercropmanager.auth.api

import hr.fer.fercropmanager.network.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders

private const val LOGIN_URL = "api/auth/login"
private const val USER_URL = "api/auth/user"

class AuthApiImpl(private val httpClient: HttpClient) : AuthApi {

    override suspend fun login(loginRequest: LoginRequest): Result<LoginDto> {
        return try {
            val response: LoginDto = httpClient.post(BASE_URL + LOGIN_URL) {
                setBody(loginRequest)
            }.body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e.fillInStackTrace())
        }
    }

    override suspend fun getUser(token: String): Result<UserDto> {
        return try {
            val response: UserDto = httpClient.get(BASE_URL + USER_URL) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }.body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e.fillInStackTrace())
        }
    }
}
