package com.example.lojasocial

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
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
import com.example.lojasocial.ui.home.UserHomeView
import com.example.lojasocial.ui.login.LoginView
import com.example.lojasocial.ui.perfil.ProfileView
import com.example.lojasocial.ui.carts.CartsView
import com.example.lojasocial.ui.products.ProductDetailView
import com.example.lojasocial.ui.products.ProductsView
import com.example.lojasocial.ui.register.RegisterView
import com.example.lojasocial.ui.register.RegisterViewModel
import com.example.lojasocial.ui.theme.LojaSocialTheme
import com.example.lojasocial.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            LojaSocialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = AppConstants.startPage,
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        // ---------- START ----------
                        composable(AppConstants.startPage) {
                            StartView(
                                onLoginClick = { navController.navigate(AppConstants.loginRoute) },
                                onRegisterClick = { navController.navigate(AppConstants.registerRoute) }
                            )
                        }

                        // ---------- LOGIN ----------
                        composable(AppConstants.loginRoute) {
                            LoginView(
                                navController = navController,
                                onLoginClick = { userType ->
                                    Log.d("MainActivity", "onLoginClick - userType recebido: '$userType'")

                                    val dest =
                                        if (userType == "admin") {
                                            Log.d("MainActivity", "Navegando para AdminHome")
                                            AppConstants.adminHome
                                        } else {
                                            Log.d("MainActivity", "Navegando para UserHome")
                                            AppConstants.home
                                        }

                                    Log.d("MainActivity", "Destino final: $dest")
                                    navController.navigate(dest) {
                                        launchSingleTop = true
                                        popUpTo(AppConstants.startPage) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // ---------- USER HOME ----------
                        composable(AppConstants.home) {
                            UserHomeView(navController = navController)
                        }

                        // ---------- ADMIN HOME ----------
                        composable(AppConstants.adminHome) {
                            AdminProtectedRoute(
                                auth = auth,
                                db = db,
                                navController = navController,
                                onNotAdmin = {
                                    navController.navigate(AppConstants.home) {
                                        popUpTo(AppConstants.adminHome) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            ) {
                                AdminStartView(navController = navController)
                            }
                        }

                        // ---------- PRODUCTS LIST ----------
                        composable(AppConstants.products) {
                            ProductsView(navController = navController)
                        }

                        // ---------- PRODUCT CREATE ----------
                        composable(AppConstants.productsDetailCreate) {
                            ProductDetailView(navController = navController, docId = null)
                        }

                        // ---------- PRODUCT EDIT ----------
                        composable(
                            route = AppConstants.productsDetail,
                            arguments = listOf(navArgument("docId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val docId = backStackEntry.arguments?.getString("docId")
                            ProductDetailView(navController = navController, docId = docId)
                        }

                        // ---------- BENEFICIARIES LIST ----------
                        composable(AppConstants.beneficiaries) {
                            BeneficiariesListView(navController = navController)
                        }

                        // ---------- BENEFICIARY CREATE ----------
                        composable(AppConstants.createBeneficiary) {
                            BeneficiaryCreateView(navController = navController)
                        }

                        // ---------- BENEFICIARY DETAIL ----------
                        composable(
                            route = AppConstants.beneficiaryDetail,
                            arguments = listOf(navArgument("docId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val docId = backStackEntry.arguments?.getString("docId") ?: return@composable
                            BeneficiaryDetailView(navController = navController, docId = docId)
                        }

                        // ---------- FAMILIES LIST ----------
                        composable(AppConstants.families) {
                            FamiliesListView(navController = navController)
                        }

                        // ---------- FAMILY CREATE ----------
                        composable(AppConstants.createFamily) {
                            FamilyCreateView(navController = navController)
                        }

                        // ---------- FAMILY DETAIL ----------
                        composable(
                            route = AppConstants.familyDetail,
                            arguments = listOf(navArgument("docId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val docId = backStackEntry.arguments?.getString("docId") ?: return@composable
                            FamilyDetailView(navController = navController, docId = docId)
                        }

                        // ---------- AGENDAS LIST ----------
                        composable(AppConstants.agendas) {
                            AgendasListView(navController = navController)
                        }
                        composable(AppConstants.agendasCreate) {
                            AgendasCreateView(navController = navController)
                        }

                        // ---------- AGENDAS DETAIL ----------
                        composable(
                            route = AppConstants.agendasDetail,
                            arguments = listOf(navArgument("docId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val docId = backStackEntry.arguments?.getString("docId") ?: return@composable
                            AgendaDetailView(navController = navController, docId = docId)
                        }

                        // ---------- PROFILE ----------
                        composable(AppConstants.profile) {
                            ProfileView(navController = navController)
                        }

                        // ---------- CARTS (STOCK) ----------
                        composable(AppConstants.carts) {
                            CartsView(navController = navController)
                        }

                        // ---------- REGISTER ----------
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
            }
        }
    }
}

@Composable
private fun AdminProtectedRoute(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    navController: NavController,
    onNotAdmin: () -> Unit,
    content: @Composable () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var isAdmin by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        Log.d("AdminProtectedRoute", "Verificando permissões - uid: $uid")

        if (uid == null) {
            Log.w("AdminProtectedRoute", "UID é NULL - redirecionando para home")
            isLoading = false
            onNotAdmin()
            return@LaunchedEffect
        }

        Log.d("AdminProtectedRoute", "Buscando documento do user no Firestore")
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                Log.d("AdminProtectedRoute", "Documento obtido - exists: ${document.exists()}")
                Log.d("AdminProtectedRoute", "Raw data: ${document.data}")
                Log.d("AdminProtectedRoute", "typeUser field raw: ${document.get("typeUser")}")

                val user = document.toObject(User::class.java)
                Log.d("AdminProtectedRoute", "User mapeado - userType: '${user?.userType}', name: '${user?.name}'")

                isAdmin = user?.userType == "admin"
                Log.d("AdminProtectedRoute", "isAdmin = $isAdmin (comparando '${user?.userType}' == 'admin')")
                isLoading = false

                if (!isAdmin) {
                    Log.w("AdminProtectedRoute", "User NÃO é admin - redirecionando para home")
                    onNotAdmin()
                } else {
                    Log.d("AdminProtectedRoute", "User é admin - mostrando conteúdo protegido")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AdminProtectedRoute", "Erro ao verificar permissões: ${exception.message}", exception)
                isLoading = false
                onNotAdmin()
            }
    }

    when {
        isLoading -> {
            Log.d("AdminProtectedRoute", "Mostrando loading spinner")
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        isAdmin -> {
            Log.d("AdminProtectedRoute", "Renderizando conteúdo admin")
            content()
        }
        else -> {
            Log.d("AdminProtectedRoute", "isAdmin=false, aguardando redirecionamento")
            // Redirecionamento já é feito no onNotAdmin
        }
    }
}