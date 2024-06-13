package hr.fer.fercropmanager.info.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.fer.fercropmanager.auth.service.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InfoViewModel(private val authService: AuthService) : ViewModel() {

    private val isLogoutDialogVisibleFlow = MutableStateFlow(false)
    private val shouldNavigateFlow = MutableStateFlow(false)

    val state = combine(
        authService.getAuthState(),
        isLogoutDialogVisibleFlow,
        shouldNavigateFlow,
    ) { authState, isLogoutDialogVisible, shouldNavigate ->
        InfoViewState(authState, isLogoutDialogVisible, shouldNavigate)
    }.stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = InfoViewState())

    fun onInteraction(interaction: InfoInteraction) {
        when (interaction) {
            InfoInteraction.DialogHide -> {
                isLogoutDialogVisibleFlow.value = false
            }
            InfoInteraction.LogoutClick -> {
                isLogoutDialogVisibleFlow.value = true
            }
            InfoInteraction.LogoutConfirm -> {
                viewModelScope.launch { authService.logout() }
                isLogoutDialogVisibleFlow.value = false
                shouldNavigateFlow.value = true
            }
        }
    }
}
