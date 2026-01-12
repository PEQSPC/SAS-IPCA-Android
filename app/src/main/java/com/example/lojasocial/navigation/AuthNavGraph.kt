package com.example.lojasocial.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.lojasocial.AppConstants
import com.example.lojasocial.ui.Start.StartView
import com.example.lojasocial.ui.login.LoginView
import com.example.lojasocial.ui.register.RegisterView
import com.example.lojasocial.ui.register.RegisterViewModel

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        startDestination = AppConstants.startPage,
        route = "authGraph"
    ) {
        // START SCREEN
        composable(AppConstants.startPage) {
            StartView(
                onLoginClick = { navController.navigate(AppConstants.loginRoute) },
                onRegisterClick = { navController.navigate(AppConstants.registerRoute) }
            )
        }

        // LOGIN SCREEN
        composable(AppConstants.loginRoute) {
            LoginView(navController = navController)
        }

        // REGISTER SCREEN
        composable(AppConstants.registerRoute) {
            val viewModel: RegisterViewModel = hiltViewModel()

            RegisterView(
                uiState = viewModel.uiState.value,
                onRegisterClick = {
                    viewModel.register(onRegisterSuccess = {
                        navController.navigate(AppConstants.loginRoute) {
                            popUpTo(AppConstants.registerRoute) { inclusive = true }
                            launchSingleTop = true
                        }
                    })
                },
                onBackToLoginClick = {
                    navController.navigate(AppConstants.loginRoute) {
                        popUpTo(AppConstants.registerRoute) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                setUserName = { viewModel.setUsername(it) },
                setEmail = { viewModel.setEmail(it) },
                setPassword = { viewModel.setPassword(it) }
            )
        }
    }
}
