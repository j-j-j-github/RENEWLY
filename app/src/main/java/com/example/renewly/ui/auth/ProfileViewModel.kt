package com.example.renewly.ui.auth

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import java.util.UUID

// The correct import to match your project's structure
import com.example.renewly.data.SupabaseClientProvider
import io.github.jan.supabase.storage.storage

// This data class will hold the state for our UI
data class ProfileUiState(
    val email: String = "Not logged in",
    val name: String = "Guest",
    val isLoggedIn: Boolean = false,
    val photoUrl: String? = null // Added a field for the profile picture URL
)

class ProfileViewModel : ViewModel() {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        fetchUserProfile()
    }

    /**
     * Uploads the profile picture to Supabase and updates the URL in Firestore.
     */
    fun uploadProfilePicture(context: Context, uri: Uri) {
        val user = auth.currentUser ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Convert Uri to ByteArray
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes != null) {
                    val fileName = "profile_pics/${user.uid}/${UUID.randomUUID()}.jpg"

                    // Corrected: Use .client to access the Supabase client
                    SupabaseClientProvider.client.storage["avatars"].upload(fileName, bytes)

                    // Corrected: Use .client to access the public URL
                    val imageUrl = SupabaseClientProvider.client.storage["avatars"].publicUrl(fileName)

                    // Update the user's document in Firestore with the new photo URL
                    val userDocument = firestore.collection("users").document(user.uid)
                    userDocument.set(mapOf("photoUrl" to imageUrl), SetOptions.merge())
                        .addOnSuccessListener {
                            // Refresh profile details to show the new picture
                            fetchUserProfile()
                            Toast.makeText(context, "Profile picture updated!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to update profile URL.", Toast.LENGTH_SHORT).show()
                        }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun updateUserName(newName: String, context: Context) {
        val user = auth.currentUser
        if (user != null && newName.isNotBlank()) {
            val userDocument = firestore.collection("users").document(user.uid)
            val userData = mapOf("name" to newName)

            userDocument.set(userData, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(context, "Name updated!", Toast.LENGTH_SHORT).show()
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
                    val photoUrl = document.getString("photoUrl")
                    _uiState.update {
                        it.copy(email = email, name = name, isLoggedIn = true, photoUrl = photoUrl)
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