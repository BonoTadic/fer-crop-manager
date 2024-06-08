package hr.fer.fercropmanager.login.service

import kotlinx.coroutines.flow.Flow

interface LoginService {

    fun getLoginStateFlow(): Flow<LoginState>
    suspend fun login(username: String, password: String)
}