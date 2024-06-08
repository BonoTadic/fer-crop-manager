package hr.fer.fercropmanager.snackbar

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SnackbarManager {
    private val _snackbarMessages = MutableStateFlow<SnackbarMessage?>(null)
    val snackbarMessages: StateFlow<SnackbarMessage?> = _snackbarMessages.asStateFlow()

    fun showMessage(message: String) {
        _snackbarMessages.value = SnackbarMessage(message)
    }

    fun clearMessage() {
        _snackbarMessages.value = null
    }

    data class SnackbarMessage(val message: String)
}
