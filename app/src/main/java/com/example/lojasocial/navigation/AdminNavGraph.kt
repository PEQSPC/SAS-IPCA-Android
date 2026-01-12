package com.example.lojasocial.navigation

import androidx.compose.foundation.layout.padding
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.lojasocial.AppConstants
import com.example.lojasocial.ui.admin.AdminStartView
import com.example.lojasocial.ui.admin.components.AdminScaffold
import com.example.lojasocial.ui.agendas.AgendaDetailView
import com.example.lojasocial.ui.agendas.AgendasCreateView
import com.example.lojasocial.ui.agendas.AgendasListView
import com.example.lojasocial.ui.beneficiary.BeneficiariesListView
import com.example.lojasocial.ui.beneficiary.BeneficiaryCreateView
import com.example.lojasocial.ui.beneficiary.BeneficiaryDetailView
import com.example.lojasocial.ui.family.FamiliesListView
import com.example.lojasocial.ui.family.FamilyCreateView
import com.example.lojasocial.ui.family.FamilyDetailView
import com.example.lojasocial.ui.perfil.ProfileView
import com.example.lojasocial.ui.products.ProductDetailView
import com.example.lojasocial.ui.products.ProductsView
import com.example.lojasocial.ui.admin.donors.DonorsListView
import com.example.lojasocial.ui.admin.donors.DonorCreateView
import com.example.lojasocial.ui.admin.donations.DonationsListView
import com.example.lojasocial.ui.admin.donations.DonationCreateView
import com.example.lojasocial.ui.admin.deliveries.DeliveriesListView
import com.example.lojasocial.ui.admin.deliveries.DeliveryCreateView
import com.example.lojasocial.ui.admin.more.MoreMenuView
import com.example.lojasocial.ui.admin.stock.StockOverviewView
import com.example.lojasocial.ui.admin.stock.StockLotsView
import com.example.lojasocial.ui.admin.stock.StockMovesView
import com.example.lojasocial.core.auth.AuthStateHolder

fun NavGraphBuilder.adminNavGraph(navController: NavHostController, authStateHolder: AuthStateHolder) {
    navigation(
        startDestination = AppConstants.adminHome,
        route = "adminGraph"
    ) {
        // ADMIN HOME (with bottom bar)
        composable(AppConstants.adminHome) {
            AdminScaffold(navController = navController, showBottomBar = true) { paddingValues ->
                AdminStartView(navController = navController, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        // PRODUCTS (with bottom bar)
        composable(AppConstants.products) {
            AdminScaffold(navController = navController, showBottomBar = true) { paddingValues ->
                ProductsView(
                    navController = navController,
                    isAdmin = true,
                    modifier = androidx.compose.ui.Modifier.padding(paddingValues)
                )
            }
        }

        // PRODUCT DETAIL/CREATE (no bottom bar - modal behavior)
        composable(AppConstants.productsDetailCreate) {
            AdminScaffold(navController = navController, showBottomBar = false) { paddingValues ->
                ProductDetailView(navController = navController, docId = null, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        composable(
            route = AppConstants.productsDetail,
            arguments = listOf(navArgument("docId") { type = NavType.StringType })
        ) { backStackEntry ->
            val docId = backStackEntry.arguments?.getString("docId")
            AdminScaffold(navController = navController, showBottomBar = false) { paddingValues ->
                ProductDetailView(navController = navController, docId = docId, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        // BENEFICIARIES (with bottom bar)
        composable(AppConstants.beneficiaries) {
            AdminScaffold(navController = navController, showBottomBar = true) { paddingValues ->
                BeneficiariesListView(navController = navController, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        // BENEFICIARY CREATE/DETAIL (no bottom bar - modal behavior)
        composable(AppConstants.createBeneficiary) {
            AdminScaffold(navController = navController, showBottomBar = false) { paddingValues ->
                BeneficiaryCreateView(navController = navController, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        composable(
            route = AppConstants.beneficiaryDetail,
            arguments = listOf(navArgument("docId") { type = NavType.StringType })
        ) { backStackEntry ->
            val docId = backStackEntry.arguments?.getString("docId") ?: return@composable
            AdminScaffold(navController = navController, showBottomBar = false) { paddingValues ->
                BeneficiaryDetailView(navController = navController, docId = docId, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        // FAMILIES (with bottom bar)
        composable(AppConstants.families) {
            AdminScaffold(navController = navController, showBottomBar = true) { paddingValues ->
                FamiliesListView(navController = navController, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        // FAMILY CREATE/DETAIL (no bottom bar - modal behavior)
        composable(AppConstants.createFamily) {
            AdminScaffold(navController = navController, showBottomBar = false) { paddingValues ->
                FamilyCreateView(navController = navController, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        composable(
            route = AppConstants.familyDetail,
            arguments = listOf(navArgument("docId") { type = NavType.StringType })
        ) { backStackEntry ->
            val docId = backStackEntry.arguments?.getString("docId") ?: return@composable
            AdminScaffold(navController = navController, showBottomBar = false) { paddingValues ->
                FamilyDetailView(navController = navController, docId = docId, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        // AGENDAS (with bottom bar)
        composable(AppConstants.agendas) {
            AdminScaffold(navController = navController, showBottomBar = true) { paddingValues ->
                AgendasListView(navController = navController, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        // AGENDA CREATE/DETAIL (no bottom bar - modal behavior)
        composable(AppConstants.agendasCreate) {
            AdminScaffold(navController = navController, showBottomBar = false) { paddingValues ->
                AgendasCreateView(navController = navController, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        composable(
            route = AppConstants.agendasDetail,
            arguments = listOf(navArgument("docId") { type = NavType.StringType })
        ) { backStackEntry ->
            val docId = backStackEntry.arguments?.getString("docId") ?: return@composable
            AdminScaffold(navController = navController, showBottomBar = false) { paddingValues ->
                AgendaDetailView(navController = navController, docId = docId, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        // DONORS (with bottom bar)
        composable(AppConstants.donors) {
            AdminScaffold(navController = navController, showBottomBar = true) { paddingValues ->
                DonorsListView(navController = navController, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        // DONOR CREATE/DETAIL (no bottom bar - modal behavior)
        composable(AppConstants.createDonor) {
            AdminScaffold(navController = navController, showBottomBar = false) { paddingValues ->
                DonorCreateView(navController = navController, docId = null, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        composable(
            route = AppConstants.donorDetail,
            arguments = listOf(navArgument("docId") { type = NavType.StringType })
        ) { backStackEntry ->
            val docId = backStackEntry.arguments?.getString("docId") ?: return@composable
            AdminScaffold(navController = navController, showBottomBar = false) { paddingValues ->
                DonorCreateView(navController = navController, docId = docId, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        // DONATIONS (with bottom bar)
        composable(AppConstants.donations) {
            AdminScaffold(navController = navController, showBottomBar = true) { paddingValues ->
                DonationsListView(navController = navController, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        // DONATION CREATE (no bottom bar - modal behavior)
        composable(AppConstants.createDonation) {
            AdminScaffold(navController = navController, showBottomBar = false) { paddingValues ->
                DonationCreateView(navController = navController, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        // DELIVERIES (with bottom bar)
        composable(AppConstants.deliveries) {
            AdminScaffold(navController = navController, showBottomBar = true) { paddingValues ->
                DeliveriesListView(navController = navController, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        // DELIVERY CREATE (no bottom bar - modal behavior)
        composable(AppConstants.createDelivery) {
            AdminScaffold(navController = navController, showBottomBar = false) { paddingValues ->
                DeliveryCreateView(navController = navController, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        // MORE MENU (with bottom bar)
        composable(AppConstants.more) {
            AdminScaffold(navController = navController, showBottomBar = true) { paddingValues ->
                MoreMenuView(
                    navController = navController,
                    authStateHolder = authStateHolder,
                    modifier = androidx.compose.ui.Modifier.padding(paddingValues)
                )
            }
        }

        // PROFILE (no bottom bar - accessed from More menu)
        composable(AppConstants.profile) {
            AdminScaffold(navController = navController, showBottomBar = false) { paddingValues ->
                ProfileView(navController = navController, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        // STOCK OVERVIEW (no bottom bar - accessed from More menu)
        composable(AppConstants.stock) {
            AdminScaffold(navController = navController, showBottomBar = false) { paddingValues ->
                StockOverviewView(navController = navController, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        // STOCK LOTS DETAIL (no bottom bar - modal behavior)
        composable(
            route = AppConstants.stockLots,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            AdminScaffold(navController = navController, showBottomBar = false) { paddingValues ->
                StockLotsView(navController = navController, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }

        // STOCK MOVES HISTORY (no bottom bar - modal behavior)
        composable(AppConstants.stockMoves) {
            AdminScaffold(navController = navController, showBottomBar = false) { paddingValues ->
                StockMovesView(navController = navController, modifier = androidx.compose.ui.Modifier.padding(paddingValues))
            }
        }
    }
}
