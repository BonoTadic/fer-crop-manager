package hr.fer.fercropmanager.login.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.materialcolors.MaterialColors
import hr.fer.fercropmanager.R
import hr.fer.fercropmanager.login.service.LoginState
import hr.fer.fercropmanager.ui.common.LoadingContent
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginContent(viewModel: LoginViewModel = koinViewModel(), onLoginSuccess: () -> Unit) {

    val state = viewModel.state.collectAsStateWithLifecycle().value

    Scaffold(containerColor = MaterialColors.LightGreen.`100`) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (state) {
                LoginViewState.Loading -> LoadingContent()
                LoginViewState.LoggedIn -> LaunchedEffect(Unit) {
                    onLoginSuccess()
                }
                is LoginViewState.LoggedOut -> {
                    Text(
                        modifier = Modifier.padding(top = 24.dp),
                        text = "FER Crop Manager",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                    )
                    Image(
                        modifier = Modifier
                            .padding(vertical = 64.dp)
                            .size(128.dp),
                        painter = painterResource(id = R.drawable.ic_fertilizer),
                        contentDescription = "Welcome Image",
                    )
                    Text(
                        text = "Enter your username and password to continue",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    OutlinedTextField(
                        modifier = Modifier.padding(vertical = 16.dp),
                        value = state.username,
                        enabled = state.loginState != LoginState.Loading,
                        isError = state.loginState == LoginState.Error,
                        singleLine = true,
                        label = { Text(text = "Username") },
                        onValueChange = { viewModel.onInteraction(LoginInteraction.UsernameChanged(it)) },
                    )
                    OutlinedTextField(
                        value = state.password,
                        enabled = state.loginState != LoginState.Loading,
                        isError = state.loginState == LoginState.Error,
                        singleLine = true,
                        label = { Text(text = "Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        onValueChange = { viewModel.onInteraction(LoginInteraction.PasswordChanged(it)) },
                    )
                    Box(
                        modifier = Modifier.heightIn(min = 80.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        when (state.loginState) {
                            LoginState.Idle, LoginState.Error -> Button(
                                modifier = Modifier.padding(vertical = 16.dp),
                                onClick = { viewModel.onInteraction(LoginInteraction.LoginClicked) },
                            ) {
                                Text(text = "Login")
                            }
                            LoginState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(vertical = 16.dp))
                            LoginState.Success -> Unit
                        }
                    }
                }
            }
        }
    }
}
