package com.example.renewly.ui.auth
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AuthScreen(onAuthed: () -> Unit) {
    val auth = remember { FirebaseAuth.getInstance() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    fun doAuth() {
        loading = true
        error = null
        val task = if (isLogin) auth.signInWithEmailAndPassword(email, password)
        else auth.createUserWithEmailAndPassword(email, password)
        task.addOnCompleteListener {
            loading = false
            if (it.isSuccessful) onAuthed() else error = it.exception?.localizedMessage
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Renewly", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(16.dp))

            ElevatedCard(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                )
            ) {
                Column(Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(Icons.Outlined.MailOutline, contentDescription = null)
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = null)
                        }
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { doAuth() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !loading
                    ) {
                        Text(if (isLogin) "Login" else "Sign Up")
                    }

                    TextButton(onClick = { isLogin = !isLogin }) {
                        Text(if (isLogin) "Need an account? Sign Up" else "Already have an account? Login")
                    }

                    if (loading) {
                        CircularProgressIndicator(Modifier.padding(top = 16.dp))
                    }

                    error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}