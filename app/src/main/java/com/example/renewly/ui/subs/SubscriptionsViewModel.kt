package com.example.renewly.ui.subs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.renewly.data.FirestoreService
import com.example.renewly.data.Subscription
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SubscriptionsViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val service = FirestoreService(auth, db)

    private val _subs = MutableStateFlow<List<Subscription>>(emptyList())
    val subs: StateFlow<List<Subscription>> = _subs

    init {
        observeSubs()
    }

    private fun observeSubs() {
        val user = auth.currentUser ?: return
        db.collection("users").document(user.uid).collection("subscriptions")
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.map { doc ->
                    doc.toObject(Subscription::class.java)!!.copy(id = doc.id)
                } ?: emptyList()
                _subs.value = list.sortedBy { it.nextDueDate }
            }
    }

    fun add(sub: Subscription) = viewModelScope.launch { service.addSubscription(sub) }
    fun update(id: String, sub: Subscription) = viewModelScope.launch { service.updateSubscription(id, sub) }
    fun delete(id: String) = viewModelScope.launch { service.deleteSubscription(id) }
}