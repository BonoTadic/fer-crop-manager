package hr.fer.fercropmanager.login.service

sealed interface LoginState {

    data object Idle : LoginState
    data object Loading : LoginState
    data object Error : LoginState
    data object Success : LoginState
}
