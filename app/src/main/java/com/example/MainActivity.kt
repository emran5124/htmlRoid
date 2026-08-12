package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.WebRunnerScreen
import com.example.ui.theme.HtmlRunnerTheme
import com.example.ui.viewmodel.WebAppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HtmlRunnerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: WebAppViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onLaunchApp = { appId ->
                    navController.navigate("webrunner/$appId")
                }
            )
        }

        composable(
            route = "webrunner/{appId}",
            arguments = listOf(navArgument("appId") { type = NavType.StringType })
        ) { backStackEntry ->
            val appId = backStackEntry.arguments?.getString("appId") ?: ""
            WebRunnerScreen(
                appId = appId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
