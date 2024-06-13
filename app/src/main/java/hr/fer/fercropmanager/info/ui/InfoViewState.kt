package hr.fer.fercropmanager.info.ui

import hr.fer.fercropmanager.auth.service.AuthState

data class InfoViewState(
    val authState: AuthState = AuthState.Loading,
    val isLogoutDialogVisible: Boolean = false,
)
