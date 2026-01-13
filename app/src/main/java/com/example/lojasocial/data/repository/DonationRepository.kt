package com.example.lojasocial.data.repository

import android.util.Log
import com.example.lojasocial.models.Donation
import com.example.lojasocial.models.DonationLine
import com.example.lojasocial.models.ResultWrapper
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DonationRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    fun getDonations(): Flow<ResultWrapper<List<Donation>>> = flow {
        try {
            Log.d("DonationRepository", "getDonations() iniciado")
            emit(ResultWrapper.Loading())

            val snapshot = db.collection("donations")
                .get()
                .await()

            val donations = snapshot.documents.mapNotNull { doc ->
                val donation = doc.toObject(Donation::class.java)
                donation?.docId = doc.id
                donation
            }

            Log.d("DonationRepository", "Donations obtidas: ${donations.size}")
            emit(ResultWrapper.Success(donations))

        } catch (e: Exception) {
            Log.e("DonationRepository", "Erro ao obter donations: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun createDonation(donation: Donation): Flow<ResultWrapper<String>> = flow {
        try {
            Log.d("DonationRepository", "createDonation() iniciado - donor: ${donation.donorName}")
            emit(ResultWrapper.Loading())

            // Generate sequential donation ID
            val counterRef = db.collection("counters").document("donations")
            val donationId = db.runTransaction { transaction ->
                val snapshot = transaction.get(counterRef)
                val currentCount = snapshot.getLong("count") ?: 0
                val nextCount = currentCount + 1

                transaction.set(counterRef, mapOf("count" to nextCount))

                "DOA-${nextCount.toString().padStart(3, '0')}"
            }.await()

            // Set donation ID and timestamps
            val now = Timestamp.now()
            donation.donationId = donationId
            donation.createdAt = now
            donation.updatedAt = now
            if (donation.status == null) {
                donation.status = "PENDING"
            }

            val docRef = db.collection("donations").add(donation).await()

            Log.d("DonationRepository", "Donation criada com ID: ${docRef.id}, donationId: $donationId")
            emit(ResultWrapper.Success(docRef.id))

        } catch (e: Exception) {
            Log.e("DonationRepository", "Erro ao criar donation: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun addDonationLine(donationId: String, line: DonationLine): Flow<ResultWrapper<Unit>> = flow {
        try {
            Log.d("DonationRepository", "addDonationLine() - donationId: $donationId, item: ${line.itemName}")
            emit(ResultWrapper.Loading())

            db.collection("donations")
                .document(donationId)
                .collection("lines")
                .add(line)
                .await()

            Log.d("DonationRepository", "DonationLine adicionada com sucesso")
            emit(ResultWrapper.Success(Unit))

        } catch (e: Exception) {
            Log.e("DonationRepository", "Erro ao adicionar donationLine: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)

    fun getDonationLines(donationId: String): Flow<ResultWrapper<List<DonationLine>>> = flow {
        try {
            Log.d("DonationRepository", "getDonationLines() - donationId: $donationId")
            emit(ResultWrapper.Loading())

            val snapshot = db.collection("donations")
                .document(donationId)
                .collection("lines")
                .get()
                .await()

            val lines = snapshot.documents.mapNotNull { doc ->
                val line = doc.toObject(DonationLine::class.java)
                line?.docId = doc.id
                line
            }

            Log.d("DonationRepository", "DonationLines obtidas: ${lines.size}")
            emit(ResultWrapper.Success(lines))

        } catch (e: Exception) {
            Log.e("DonationRepository", "Erro ao obter donationLines: ${e.message}", e)
            emit(ResultWrapper.Error(e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)
}
