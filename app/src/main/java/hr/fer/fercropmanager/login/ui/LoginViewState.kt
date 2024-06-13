package hr.fer.fercropmanager.login.ui

import hr.fer.fercropmanager.login.service.LoginState

sealed interface LoginViewState {

    data object Loading : LoginViewState
    data object LoggedIn : LoginViewState
    data class LoggedOut(val loginState: LoginState, val username: String, val password: String) : LoginViewState
}
