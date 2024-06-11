package hr.fer.fercropmanager.snackbar.ui

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hr.fer.fercropmanager.snackbar.SnackbarManager

@Composable
fun SnackbarHost(snackbarManager: SnackbarManager) {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentMessage by snackbarManager.snackbarMessages.collectAsStateWithLifecycle()

    LaunchedEffect(currentMessage) {
        currentMessage?.let { message ->
            val result = snackbarHostState.showSnackbar(message.message)
            if (result == SnackbarResult.Dismissed || result == SnackbarResult.ActionPerformed) {
                snackbarManager.clearMessage()
            }
        }
    }

    SnackbarHost(hostState = snackbarHostState)
}
