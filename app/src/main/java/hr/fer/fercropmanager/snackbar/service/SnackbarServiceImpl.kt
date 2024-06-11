package hr.fer.fercropmanager.snackbar.service

import hr.fer.fercropmanager.snackbar.SnackbarManager

class SnackbarServiceImpl(private val snackbarManager: SnackbarManager) : SnackbarService {

    override fun notifyUser(message: String) {
        snackbarManager.showMessage(message)
    }
}
