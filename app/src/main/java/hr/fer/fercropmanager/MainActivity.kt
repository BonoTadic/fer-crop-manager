package hr.fer.fercropmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hr.fer.fercropmanager.crop.ui.CropContent
import hr.fer.fercropmanager.login.ui.LoginContent
import hr.fer.fercropmanager.ui.theme.FERCropManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FERCropManagerTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable(
                        route = "login",
                        exitTransition = {
                            fadeOut(
                                animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                            ) + slideOutOfContainer(
                                animationSpec = tween(durationMillis = 300, easing = EaseOut),
                                towards = AnimatedContentTransitionScope.SlideDirection.Start
                            )
                        }
                    ) {
                        LoginContent(
                            onLoginSuccess = {
                                navController.navigate("crop") {
                                    popUpTo("login") { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                        )
                    }
                    composable("crop") {
                        CropContent()
                    }
                }
            }
        }
    }
}