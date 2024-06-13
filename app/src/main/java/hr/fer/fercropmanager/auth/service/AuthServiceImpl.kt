package hr.fer.fercropmanager.auth.service

import hr.fer.fercropmanager.auth.api.AuthApi
import hr.fer.fercropmanager.auth.api.LoginRequest
import hr.fer.fercropmanager.auth.persistence.AuthPersistence

class AuthServiceImpl(
    private val authApi: AuthApi,
    private val authPersistence: AuthPersistence,
) : AuthService {

    override fun getAuthState() = authPersistence.authStateFlow

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
