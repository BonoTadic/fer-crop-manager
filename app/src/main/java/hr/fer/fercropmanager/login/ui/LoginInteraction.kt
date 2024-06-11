package hr.fer.fercropmanager.login.ui

sealed interface LoginInteraction {

    data object LoginClicked : LoginInteraction
    data class UsernameChanged(val username: String) : LoginInteraction
    data class PasswordChanged(val password: String) : LoginInteraction
}
