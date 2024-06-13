package hr.fer.fercropmanager.auth.service

import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthState {

    @Serializable
    data object Idle : AuthState
    @Serializable
    data object Loading : AuthState
    @Serializable
    data object Error : AuthState
    @Serializable
    data class Success(
        val token: String,
        val customerId: String,
        val firstName: String,
        val lastName: String,
        val email: String,
    ) : AuthState
}
