package hr.fer.fercropmanager.login.ui

import hr.fer.fercropmanager.login.service.LoginState

data class LoginViewState(
    val loginState: LoginState,
    val username: String,
    val password: String,
)