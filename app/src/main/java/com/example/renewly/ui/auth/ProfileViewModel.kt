package com.example.renewly.ui.auth

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// This data class will hold the state for our UI
data class ProfileUiState(
    val email: String = "Not logged in",
    val name: String = "Guest",
    val isLoggedIn: Boolean = false
)

class ProfileViewModel : ViewModel() {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        fetchUserProfile()
    }

    fun updateUserName(newName: String, context: Context) {
        val user = auth.currentUser
        if (user != null && newName.isNotBlank()) {
            val userDocument = firestore.collection("users").document(user.uid)
            val userData = mapOf("name" to newName)

            // --- THIS IS THE FIX ---
            // Use .set with merge to create the doc if it's missing, or update it if it exists.
            userDocument.set(userData, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(context, "Name updated!", Toast.LENGTH_SHORT).show()
                    // Refresh the user profile to show the new name immediately
                    fetchUserProfile()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun fetchUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email ?: "No Email"
            val uid = currentUser.uid

            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    val name = document.getString("name") ?: "No Name Set"
                    _uiState.update {
                        it.copy(email = email, name = name, isLoggedIn = true)
                    }
                }
                .addOnFailureListener {
                    _uiState.update {
                        it.copy(email = email, name = "Could not load name", isLoggedIn = true)
                    }
                }
        } else {
            _uiState.update { ProfileUiState() }
        }
    }

    fun sendPasswordReset(context: Context) {
        val email = auth.currentUser?.email
        if (email != null) {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Password reset email sent.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to send reset email.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, "You're not logged in.", Toast.LENGTH_SHORT).show()
        }
    }
}