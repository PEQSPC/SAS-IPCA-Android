package com.example.lojasocial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lojasocial.ui.Start.StartView
import com.example.lojasocial.ui.admin.AdminStartView
import com.example.lojasocial.ui.agendas.AgendaDetailView
import com.example.lojasocial.ui.agendas.AgendasCreateView
import com.example.lojasocial.ui.agendas.AgendasListView
import com.example.lojasocial.ui.beneficiary.BeneficiariesListView
import com.example.lojasocial.ui.beneficiary.BeneficiaryCreateView
import com.example.lojasocial.ui.beneficiary.BeneficiaryDetailView
import com.example.lojasocial.ui.family.FamiliesListView
import com.example.lojasocial.ui.family.FamilyCreateView
import com.example.lojasocial.ui.family.FamilyDetailView
import com.example.lojasocial.ui.login.LoginView
import com.example.lojasocial.ui.perfil.ProfileView
import com.example.lojasocial.ui.products.ProductDetailView
import com.example.lojasocial.ui.products.ProductsView
import com.example.lojasocial.ui.register.RegisterView
import com.example.lojasocial.ui.register.RegisterViewModel
import com.example.lojasocial.ui.theme.LojaSocialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            LojaSocialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = AppConstansts.startPage,
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        // ---------- START ----------
                        composable(AppConstansts.startPage) {
                            StartView(
                                onLoginClick = { navController.navigate(AppConstansts.loginRoute) },
                                onRegisterClick = { navController.navigate(AppConstansts.registerRoute) }
                            )
                        }

                        // ---------- LOGIN ----------
                        composable(AppConstansts.loginRoute) {
                            LoginView(
                                navController = navController,
                                onLoginClick = { userType ->
                                    val dest =
                                        if (userType == "admin") AppConstansts.adminHome else AppConstansts.home

                                    navController.navigate(dest) {
                                        launchSingleTop = true
                                        popUpTo(AppConstansts.startPage) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // ---------- USER HOME ----------
                        composable(AppConstansts.home) {
                            Text("Este é um utilizador")
                        }

                        // ---------- ADMIN HOME ----------
                        composable(AppConstansts.adminHome) {
                            AdminStartView(navController = navController)
                        }

                        // ---------- PRODUCTS LIST ----------
                        composable(AppConstansts.products) {
                            ProductsView(navController = navController)
                        }

                        // ---------- PRODUCT CREATE ----------
                        composable(AppConstansts.productsDetailCreate) {
                            ProductDetailView(navController = navController, docId = null)
                        }

                        // ---------- PRODUCT EDIT ----------
                        composable(
                            route = AppConstansts.productsDetail,
                            arguments = listOf(navArgument("docId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val docId = backStackEntry.arguments?.getString("docId")
                            ProductDetailView(navController = navController, docId = docId)
                        }

                        // ---------- BENEFICIARIES LIST ----------
                        composable(AppConstansts.beneficiaries) {
                            BeneficiariesListView(navController = navController)
                        }

                        // ---------- BENEFICIARY CREATE ----------
                        composable(AppConstansts.createBeneficiary) {
                            BeneficiaryCreateView(navController = navController)
                        }

                        // ---------- BENEFICIARY DETAIL ----------
                        composable(
                            route = AppConstansts.beneficiaryDetail,
                            arguments = listOf(navArgument("docId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val docId = backStackEntry.arguments?.getString("docId") ?: return@composable
                            BeneficiaryDetailView(navController = navController, docId = docId)
                        }

                        // ---------- FAMILIES LIST ----------
                        composable(AppConstansts.families) {
                            FamiliesListView(navController = navController)
                        }

                        // ---------- FAMILY CREATE ----------
                        composable(AppConstansts.createFamily) {
                            FamilyCreateView(navController = navController)
                        }

                        // ---------- FAMILY DETAIL ----------
                        composable(
                            route = AppConstansts.familyDetail,
                            arguments = listOf(navArgument("docId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val docId = backStackEntry.arguments?.getString("docId") ?: return@composable
                            FamilyDetailView(navController = navController, docId = docId)
                        }

                        // ---------- AGENDAS LIST ----------
                        composable(AppConstansts.agendas) {
                            AgendasListView(navController = navController)
                        }
                        composable(AppConstansts.agendasCreate) {
                            AgendasCreateView(navController = navController)
                        }

                        // ---------- AGENDAS DETAIL ----------
                        composable(
                            route = AppConstansts.agendasDetail,
                            arguments = listOf(navArgument("docId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val docId = backStackEntry.arguments?.getString("docId") ?: return@composable
                            AgendaDetailView(navController = navController, docId = docId)
                        }

                        // ---------- PROFILE ----------
                        composable(AppConstansts.profile) {
                            ProfileView(navController = navController)
                        }

                        // ---------- REGISTER ----------
                        composable(AppConstansts.registerRoute) {
                            val viewModel: RegisterViewModel = hiltViewModel()

                            RegisterView(
                                uiState = viewModel.uiState.value,
                                onRegisterClick = {
                                    viewModel.register(onRegisterSuccess = {
                                        navController.navigate(AppConstansts.loginRoute) {
                                            popUpTo(AppConstansts.registerRoute) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    })
                                },
                                onBackToLoginClick = {
                                    navController.navigate(AppConstansts.loginRoute) {
                                        popUpTo(AppConstansts.registerRoute) { inclusive = true }
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
            }
        }
    }
}