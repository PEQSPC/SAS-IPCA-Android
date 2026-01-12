package com.example.lojasocial

import com.example.lojasocial.core.auth.AuthStateHolder
import com.example.lojasocial.data.repository.AgendaRepository
import com.example.lojasocial.data.repository.BeneficiaryRepository
import com.example.lojasocial.data.repository.DeliveryRepository
import com.example.lojasocial.data.repository.DonationRepository
import com.example.lojasocial.data.repository.DonorRepository
import com.example.lojasocial.data.repository.FamilyRepository
import com.example.lojasocial.data.repository.ItemRepository
import com.example.lojasocial.data.repository.StockLotRepository
import com.example.lojasocial.data.repository.StockMoveRepository
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

    @Provides
    @Singleton
    fun providesAuthStateHolder(
        auth: FirebaseAuth,
        db: FirebaseFirestore
    ): AuthStateHolder {
        return AuthStateHolder(auth, db)
    }

    @Provides
    @Singleton
    fun providesDonorRepository(db: FirebaseFirestore): DonorRepository {
        return DonorRepository(db)
    }

    @Provides
    @Singleton
    fun providesStockLotRepository(db: FirebaseFirestore): StockLotRepository {
        return StockLotRepository(db)
    }

    @Provides
    @Singleton
    fun providesDonationRepository(db: FirebaseFirestore): DonationRepository {
        return DonationRepository(db)
    }

    @Provides
    @Singleton
    fun providesDeliveryRepository(db: FirebaseFirestore): DeliveryRepository {
        return DeliveryRepository(db)
    }

    @Provides
    @Singleton
    fun providesStockMoveRepository(db: FirebaseFirestore): StockMoveRepository {
        return StockMoveRepository(db)
    }

    @Provides
    @Singleton
    fun providesBeneficiaryRepository(db: FirebaseFirestore): BeneficiaryRepository {
        return BeneficiaryRepository(db)
    }

    @Provides
    @Singleton
    fun providesFamilyRepository(db: FirebaseFirestore): FamilyRepository {
        return FamilyRepository(db)
    }

    @Provides
    @Singleton
    fun providesAgendaRepository(db: FirebaseFirestore): AgendaRepository {
        return AgendaRepository(db)
    }

    @Provides
    @Singleton
    fun providesItemRepository(db: FirebaseFirestore): ItemRepository {
        return ItemRepository(db)
    }
}