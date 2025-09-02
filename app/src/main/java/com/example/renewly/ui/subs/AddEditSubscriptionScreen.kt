package com.example.renewly.ui.subs
import com.example.renewly.data.CycleType
import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.renewly.data.Subscription
import com.example.renewly.data.SupabaseClientProvider
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.UUID

fun uploadIconToSupabase(context: Context, uri: Uri, onComplete: (String?) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)!!
            val bytes = inputStream.readBytes()
            inputStream.close()

            val fileName = "${UUID.randomUUID()}.png"
            // --- THIS IS THE FIX ---
            // The bucket name must exactly match the one in your Supabase dashboard.
            val bucket = SupabaseClientProvider.client.storage.from("renewly_app_icons")
            // ----------------------

            bucket.upload(fileName, bytes, upsert = true)
            val url = bucket.publicUrl(fileName)

            withContext(Dispatchers.Main) {
                onComplete(url)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                onComplete(null)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSubscriptionScreen(
    original: Subscription? = null,
    onSave: (Subscription) -> Unit,
    onCancel: () -> Unit,
    onDelete: (Subscription) -> Unit
) {
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var purchaseDate by rememberSaveable { mutableStateOf(Calendar.getInstance().timeInMillis) }
    var cycleType by rememberSaveable { mutableStateOf(CycleType.MONTHLY) }
    var iconKey by rememberSaveable { mutableStateOf("🎯") }
    var iconUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var uploading by remember { mutableStateOf(false) }

    LaunchedEffect(original) {
        if (original != null) {
            name = original.name
            price = original.price.toString()
            purchaseDate = original.nextDueDate
            cycleType = if (original.cycleInDays >= 365) CycleType.YEARLY else CycleType.MONTHLY
            iconKey = original.iconKey
            iconUri = original.iconUri?.let { Uri.parse(it) }
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        iconUri = uri
    }

    val calendar = Calendar.getInstance().apply { timeInMillis = purchaseDate }
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day, 0, 0, 0)
            purchaseDate = calendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (original == null) "Add Subscription" else "Edit Subscription") },
                actions = {
                    if (original != null) {
                        IconButton(onClick = { onDelete(original) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Subscription")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { input -> name = input },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = price,
                onValueChange = { input -> price = input.filter { ch -> ch.isDigit() || ch == '.' } },
                label = { Text("Price") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    selected = cycleType == CycleType.MONTHLY,
                    onClick = { cycleType = CycleType.MONTHLY },
                    label = { Text("Monthly") }
                )
                FilterChip(
                    selected = cycleType == CycleType.YEARLY,
                    onClick = { cycleType = CycleType.YEARLY },
                    label = { Text("Yearly") }
                )
            }

            Button(onClick = { datePicker.show() }) {
                val selectedDate = Calendar.getInstance().apply { timeInMillis = purchaseDate }
                Text("Start Date: ${selectedDate.get(Calendar.DAY_OF_MONTH)}/${selectedDate.get(Calendar.MONTH) + 1}/${selectedDate.get(Calendar.YEAR)}")
            }

            OutlinedTextField(
                value = iconKey,
                onValueChange = { input -> iconKey = input.take(2) },
                label = { Text("Icon (emoji or 1-2 chars)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                    Text("Pick Icon Image")
                }
                if (iconUri != null) {
                    AsyncImage(model = iconUri, contentDescription = "Selected Icon", modifier = Modifier.size(40.dp))
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    enabled = !uploading,
                    onClick = {
                        uploading = true

                        val calcCalendar = Calendar.getInstance().apply { timeInMillis = purchaseDate }
                        if (cycleType == CycleType.MONTHLY) {
                            calcCalendar.add(Calendar.MONTH, 1)
                        } else {
                            calcCalendar.add(Calendar.YEAR, 1)
                        }

                        if (iconUri != null && iconUri.toString().startsWith("content://")) {
                            uploadIconToSupabase(context, iconUri!!) { uploadedUrl: String? ->
                                val sub = Subscription(
                                    id = original?.id ?: "",
                                    name = name.trim(),
                                    price = price.toDoubleOrNull() ?: 0.0,
                                    cycleInDays = if (cycleType == CycleType.YEARLY) 365 else 30,
                                    nextDueDate = calcCalendar.timeInMillis,
                                    iconKey = if (uploadedUrl == null) iconKey.ifBlank { "📦" } else "",
                                    iconUri = uploadedUrl
                                )
                                uploading = false
                                onSave(sub)
                            }
                        } else {
                            val sub = Subscription(
                                id = original?.id ?: "",
                                name = name.trim(),
                                price = price.toDoubleOrNull() ?: 0.0,
                                cycleInDays = if (cycleType == CycleType.YEARLY) 365 else 30,
                                nextDueDate = calcCalendar.timeInMillis,
                                iconKey = original?.iconKey ?: iconKey.ifBlank { "📦" },
                                iconUri = original?.iconUri
                            )
                            uploading = false
                            onSave(sub)
                        }
                    }
                ) {
                    Text(if (uploading) "Uploading..." else "Save")
                }

                OutlinedButton(onClick = onCancel) { Text("Cancel") }
            }
        }
    }
}
