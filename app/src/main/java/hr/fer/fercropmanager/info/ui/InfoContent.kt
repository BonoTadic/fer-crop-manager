package hr.fer.fercropmanager.info.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hr.fer.fercropmanager.R
import hr.fer.fercropmanager.auth.service.AuthState
import hr.fer.fercropmanager.ui.common.ErrorContent
import hr.fer.fercropmanager.ui.common.LoadingContent
import hr.fer.fercropmanager.ui.common.TopBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun InfoContent(viewModel: InfoViewModel = koinViewModel(), onBackClick: () -> Unit, onLogout: () -> Unit) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(state.shouldNavigate) {
        if (state.shouldNavigate) onLogout()
    }

    Scaffold(
        topBar = { TopBar(title = "User information", onBackClick = onBackClick) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            when (state.authState) {
                AuthState.Idle -> Unit
                AuthState.Loading -> LoadingContent()
                AuthState.Error -> ErrorContent(onRetry = {})
                is AuthState.Success -> LoadedContent(
                    authState = state.authState,
                    isLogoutDialogVisible = state.isLogoutDialogVisible,
                    onLogout = { viewModel.onInteraction(InfoInteraction.LogoutClick) },
                    onLogoutConfirm = { viewModel.onInteraction(InfoInteraction.LogoutConfirm) },
                    onDialogHide = { viewModel.onInteraction(InfoInteraction.DialogHide) },
                )
            }
        }
    }
}

@Composable
private fun LoadedContent(
    authState: AuthState.Success,
    isLogoutDialogVisible: Boolean,
    onLogout: () -> Unit,
    onLogoutConfirm: () -> Unit,
    onDialogHide: () -> Unit,
) {
    if (isLogoutDialogVisible) {
        LogoutDialog(onConfirm = onLogoutConfirm, onCancel = onDialogHide)
    }
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        UserContent(authState = authState)
        OutlinedButton(
            modifier = Modifier.padding(vertical = 16.dp),
            onClick = onLogout,
        ) {
            Text(text = "Log out")
        }
    }
}

@Composable
private fun UserContent(authState: AuthState.Success) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    modifier = Modifier
                        .size(128.dp)
                        .padding(bottom = 16.dp)
                        .align(Alignment.Center),
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = "User Icon",
                )
            }
            Column {
                Text(
                    text = "${authState.firstName} ${authState.lastName}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = authState.email,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    color = Color.Gray,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogoutDialog(onConfirm: () -> Unit, onCancel: () -> Unit) {
    BasicAlertDialog(onDismissRequest = {}) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 12.dp),
                    text = "Confirm log out",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(text = "You will be redirected to the login screen.")
                OutlinedButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = onConfirm,
                ) {
                    Text(text = "Confirm")
                }
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = onCancel,
                ) {
                    Text(text = "Cancel")
                }
            }
        }
    }
}
