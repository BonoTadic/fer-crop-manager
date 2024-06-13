package hr.fer.fercropmanager.auth.service

import kotlinx.coroutines.flow.Flow

interface AuthService {

    fun getAuthState(): Flow<AuthState>
    suspend fun login(username: String, password: String)
    suspend fun logout()
}
