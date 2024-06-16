package hr.fer.fercropmanager.auth.service

import hr.fer.fercropmanager.auth.api.AuthApi
import hr.fer.fercropmanager.auth.api.LoginRequest
import hr.fer.fercropmanager.auth.persistence.AuthPersistence
import kotlinx.coroutines.flow.map

private const val DAY_TO_MILLIS = 86_400_000L

class AuthServiceImpl(
    private val authApi: AuthApi,
    private val authPersistence: AuthPersistence,
) : AuthService {

    override fun getAuthState() = authPersistence.authStateFlow.map { authState ->
        when {
            authState !is AuthState.Success -> authState
            System.currentTimeMillis() - authState.timestamp > DAY_TO_MILLIS -> {
                authPersistence.updateAuthState(AuthState.Idle)
                AuthState.Idle
            }
            else -> authState
        }
    }

    override suspend fun login(username: String, password: String) {
        authPersistence.updateAuthState(AuthState.Loading)
        authApi.login(LoginRequest(username, password)).fold(
            onSuccess = { fetchUser(it.token) },
            onFailure = { authPersistence.updateAuthState(AuthState.Error) },
        )
    }

    override suspend fun logout() {
        authPersistence.updateAuthState(AuthState.Idle)
    }

    private suspend fun fetchUser(token: String) {
        authApi.getUser(token).fold(
            onSuccess = {
                authPersistence.updateAuthState(
                    authState = AuthState.Success(
                        token = token,
                        timestamp = System.currentTimeMillis(),
                        customerId = it.customerId.id,
                        email = it.email,
                        firstName = it.firstName,
                        lastName = it.lastName,
                    )
                )
            },
            onFailure = { authPersistence.updateAuthState(AuthState.Error) },
        )
    }
}
