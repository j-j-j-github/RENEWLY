package com.example.renewly.ui.auth

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.renewly.data.SupabaseClientProvider
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

// UI state for the profile
data class ProfileUiState(
    val email: String = "Not logged in",
    val name: String = "Guest",
    val isLoggedIn: Boolean = false,
    val photoUrl: String? = null
)

class ProfileViewModel : ViewModel() {

    private val auth = Firebase.auth

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        fetchUserProfile()
    }

    /**
     * Uploads the profile picture to the same bucket as subscription icons.
     */
    fun uploadProfilePicture(context: Context, uri: Uri) {
        val userId = auth.currentUser?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes != null) {
                    // Use the existing icons bucket
                    val fileName = "profiles/$userId-${UUID.randomUUID()}.png"
                    val bucket = SupabaseClientProvider.client.storage.from("renewly_app_icons")
                    bucket.upload(fileName, bytes, upsert = true)

                    val url = bucket.publicUrl(fileName)  // <-- old working syntax

                    // Update the ViewModel state so sidebar recomposes
                    withContext(Dispatchers.Main) {
                        _uiState.update { it.copy(photoUrl = url) }
                        Toast.makeText(context, "Profile picture updated!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * Updates the user's display name locally in the ViewModel.
     */
    fun updateUserName(newName: String, context: Context) {
        if (newName.isNotBlank()) {
            _uiState.update { it.copy(name = newName) }
            Toast.makeText(context, "Name updated!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Fetches basic profile info from Firebase Auth.
     */
    fun fetchUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            _uiState.update {
                it.copy(
                    email = currentUser.email ?: "No Email",
                    name = it.name,
                    isLoggedIn = true
                )
            }
        } else {
            _uiState.update { ProfileUiState() }
        }
    }

    /**
     * Sends a password reset email via Firebase Auth.
     */
    fun sendPasswordReset(context: Context) {
        val email = auth.currentUser?.email
        if (email != null) {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    Toast.makeText(
                        context,
                        if (task.isSuccessful) "Password reset email sent." else "Failed to send reset email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(context, "You're not logged in.", Toast.LENGTH_SHORT).show()
        }
    }
}