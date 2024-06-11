package hr.fer.fercropmanager.auth.api

interface AuthApi {

    suspend fun login(loginRequest: LoginRequest): Result<LoginDto>
    suspend fun getUser(token: String): Result<UserDto>
}
