package hr.fer.fercropmanager.auth.persistence

import hr.fer.fercropmanager.auth.service.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthPersistence {

    val authStateFlow: Flow<AuthState>
    suspend fun updateAuthState(authState: AuthState)
}