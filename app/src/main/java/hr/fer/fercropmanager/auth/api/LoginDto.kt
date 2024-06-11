package hr.fer.fercropmanager.auth.api

import kotlinx.serialization.Serializable

@Serializable
data class LoginDto(val token: String, val refreshToken: String)

@Serializable
data class LoginRequest(val username: String, val password: String)
