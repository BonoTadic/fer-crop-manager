package hr.fer.fercropmanager.auth.service

sealed interface AuthState {

    data object Idle : AuthState
    data object Loading : AuthState
    data object Error : AuthState
    data class Success(
        val token: String,
        val customerId: String,
        val firstName: String,
        val lastName: String,
        val email: String,
    ) : AuthState
}
