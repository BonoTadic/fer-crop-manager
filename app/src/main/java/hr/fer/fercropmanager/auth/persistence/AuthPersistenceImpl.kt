package hr.fer.fercropmanager.auth.persistence

import hr.fer.fercropmanager.auth.service.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthPersistenceImpl : AuthPersistence {

    private val authStateCache = MutableStateFlow<AuthState>(AuthState.Idle)

    override val authStateFlow = authStateCache.asStateFlow()

    override suspend fun updateAuthState(authState: AuthState) {
        authStateCache.value = authState
    }
}
