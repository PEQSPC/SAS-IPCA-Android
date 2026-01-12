package com.example.lojasocial.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.lojasocial.AppConstants
import com.example.lojasocial.ui.carts.CartsView
import com.example.lojasocial.ui.home.UserHomeView
import com.example.lojasocial.ui.perfil.ProfileView
import com.example.lojasocial.ui.products.ProductDetailView
import com.example.lojasocial.ui.products.ProductsView

fun NavGraphBuilder.userNavGraph(navController: NavHostController) {
    navigation(
        startDestination = AppConstants.home,
        route = "userGraph"
    ) {
        // USER HOME
        composable(AppConstants.home) {
            UserHomeView(navController = navController)
        }

        // PRODUCTS
        composable(AppConstants.products) {
            ProductsView(navController = navController)
        }

        composable(
            route = AppConstants.productsDetail,
            arguments = listOf(navArgument("docId") { type = NavType.StringType })
        ) { backStackEntry ->
            val docId = backStackEntry.arguments?.getString("docId")
            ProductDetailView(navController = navController, docId = docId)
        }

        // CARTS (STOCK)
        composable(AppConstants.carts) {
            CartsView(navController = navController)
        }

        // PROFILE
        composable(AppConstants.profile) {
            ProfileView(navController = navController)
        }
    }
}
