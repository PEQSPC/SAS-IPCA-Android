package com.example.lojasocial

import com.example.lojasocial.models.AuthRepository
import com.example.lojasocial.models.LoginRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun firestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun auth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun providesLoginRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
    ): LoginRepository {
        return AuthRepository(
            firebaseAuth,
            firestore,
        )
    }
}