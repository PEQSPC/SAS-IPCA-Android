package com.example.lojasocial.core.auth

import android.util.Log
import com.example.lojasocial.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStateHolder @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                Log.d(TAG, "Auth state changed: User logged in (UID: ${firebaseUser.uid})")
                loadUser(firebaseUser.uid)
            } else {
                Log.d(TAG, "Auth state changed: User logged out")
                clearState()
            }
        }
    }

    private fun loadUser(uid: String) {
        scope.launch {
            try {
                _authState.value = AuthState.Loading
                Log.d(TAG, "Loading user data for UID: $uid")

                val document = db.collection("users")
                    .document(uid)
                    .get()
                    .await()

                val user = document.toObject(User::class.java)?.apply {
                    docId = document.id
                }

                if (user != null) {
                    _currentUser.value = user
                    _isAdmin.value = user.userType == "admin"
                    _authState.value = AuthState.Authenticated(user)
                    Log.d(TAG, "User loaded successfully: ${user.name}, isAdmin: ${_isAdmin.value}")
                } else {
                    val errorMsg = "User document not found"
                    Log.e(TAG, errorMsg)
                    _authState.value = AuthState.Error(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Failed to load user: ${e.message}"
                Log.e(TAG, errorMsg, e)
                _authState.value = AuthState.Error(errorMsg)
            }
        }
    }

    fun refresh() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            Log.d(TAG, "Refreshing user data")
            loadUser(uid)
        } else {
            Log.w(TAG, "Cannot refresh: No user logged in")
        }
    }

    fun signOut() {
        Log.d(TAG, "Signing out")
        auth.signOut()
        clearState()
    }

    private fun clearState() {
        _currentUser.value = null
        _isAdmin.value = false
        _authState.value = AuthState.Unauthenticated
        Log.d(TAG, "State cleared")
    }

    companion object {
        private const val TAG = "AuthStateHolder"
    }
}
