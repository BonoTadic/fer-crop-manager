package hr.fer.fercropmanager.login.service

import hr.fer.fercropmanager.auth.service.AuthService
import hr.fer.fercropmanager.auth.service.AuthState
import kotlinx.coroutines.flow.map

class LoginServiceImpl(private val authService: AuthService) : LoginService {

    override fun getLoginStateFlow() = authService.getAuthState().map { authState ->
        when (authState) {
            AuthState.Error -> LoginState.Error
            AuthState.Idle -> LoginState.Idle
            AuthState.Loading -> LoginState.Loading
            is AuthState.Success -> LoginState.Success
        }
    }

    override suspend fun login(username: String, password: String) {
        authService.login(username, password)
    }
}
