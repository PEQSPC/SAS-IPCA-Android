package com.example.lojasocial.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.lojasocial.core.auth.AuthState
import com.example.lojasocial.core.auth.AuthStateHolder

@Composable
fun RootNavGraph(
    navController: NavHostController,
    authStateHolder: AuthStateHolder
) {
    val authState by authStateHolder.authState.collectAsState()
    val isAdmin by authStateHolder.isAdmin.collectAsState()

    // Observe auth state changes and navigate accordingly
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                val targetGraph = if (isAdmin) "adminGraph" else "userGraph"
                val currentRoute = navController.currentBackStackEntry?.destination?.route

                // Only navigate if we're not already in the target graph
                if (currentRoute != targetGraph) {
                    navController.navigate(targetGraph) {
                        popUpTo("authGraph") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            is AuthState.Unauthenticated -> {
                val currentRoute = navController.currentBackStackEntry?.destination?.parent?.route

                // Only navigate to authGraph if we're not already there
                if (currentRoute != "authGraph") {
                    navController.navigate("authGraph") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            is AuthState.Loading, is AuthState.Error -> {
                // Stay on current screen during loading or error
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "authGraph"
    ) {
        authNavGraph(navController)
        adminNavGraph(navController, authStateHolder)
        userNavGraph(navController)
    }
}
