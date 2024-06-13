package hr.fer.fercropmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import hr.fer.fercropmanager.alarms.ui.details.AlarmDetailsContent
import hr.fer.fercropmanager.alarms.ui.list.AlarmsListContent
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
                            exitTransition(towards = AnimatedContentTransitionScope.SlideDirection.Start)
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
                    composable(route = "crop") {
                        CropContent(onAlarmIconClick = { navController.navigate("alarms") })
                    }
                    composable(
                        route = "alarms",
                        enterTransition = { enterTransition() },
                        popExitTransition = { exitTransition() },
                        popEnterTransition = null,
                    ) {
                        AlarmsListContent(
                            onBackClick = { navController.popBackStack() },
                            onAlarmClick = { navController.navigate("alarm/${it}") },
                        )
                    }
                    composable(
                        route = "alarm/{alarmId}",
                        arguments = listOf(navArgument("alarmId") { type = NavType.StringType }),
                        enterTransition = { enterTransition() },
                        popExitTransition = { exitTransition() },
                    ) { backStackEntry ->
                        AlarmDetailsContent(
                            alarmId = backStackEntry.arguments?.getString("alarmId")!!,
                            onBackClick = { navController.popBackStack() },
                        )
                    }
                }
            }
        }
    }

    private fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition() = fadeIn(
        animationSpec = tween(durationMillis = 300, easing = LinearEasing)
    ) + slideIntoContainer(
        animationSpec = tween(300, easing = EaseIn),
        towards = AnimatedContentTransitionScope.SlideDirection.Start,
    )

    private fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(
        towards: AnimatedContentTransitionScope.SlideDirection = AnimatedContentTransitionScope.SlideDirection.End,
    ) = fadeOut(
        animationSpec = tween(durationMillis = 300, easing = LinearEasing)
    ) + slideOutOfContainer(
        animationSpec = tween(300, easing = EaseOut),
        towards = towards,
    )
}
