package com.example.renewly.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreService(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun userSubs() = db.collection("users")
        .document(requireNotNull(auth.currentUser).uid)
        .collection("subscriptions")

    suspend fun addSubscription(sub: Subscription) {
        userSubs().add(sub).await()
    }

    suspend fun updateSubscription(id: String, sub: Subscription) {
        userSubs().document(id).set(sub.copy(id = id)).await()
    }

    suspend fun deleteSubscription(id: String) {
        userSubs().document(id).delete().await()
    }
}