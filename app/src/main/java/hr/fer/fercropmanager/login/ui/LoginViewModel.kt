package hr.fer.fercropmanager.login.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.fer.fercropmanager.login.service.LoginService
import hr.fer.fercropmanager.login.service.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoginViewModel(private val loginService: LoginService) : ViewModel() {

    private val usernameStateFlow = MutableStateFlow("bono.tadic@fer.hr")
    private val passwordStateFlow = MutableStateFlow("InternetStvari123")

    val state = combine(
        loginService.getLoginStateFlow(),
        usernameStateFlow,
        passwordStateFlow,
    ) { loginState, username, password ->
        LoginViewState(loginState, username, password)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = LoginViewState(LoginState.Idle, usernameStateFlow.value, passwordStateFlow.value),
    )

    fun onInteraction(interaction: LoginInteraction) {
        when (interaction) {
            LoginInteraction.LoginClicked -> viewModelScope.launch {
                loginService.login(usernameStateFlow.value, passwordStateFlow.value)
            }
            is LoginInteraction.PasswordChanged -> passwordStateFlow.value = interaction.password
            is LoginInteraction.UsernameChanged -> usernameStateFlow.value = interaction.username
        }
    }
}