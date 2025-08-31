package com.example.renewly.ui.subs
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.renewly.data.Subscription
import java.util.Calendar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSubscriptionScreen(
    original: Subscription? = null,
    onSave: (Subscription) -> Unit,
    onCancel: () -> Unit,
    onDelete: (Subscription) -> Unit // ADDED: Parameter for delete action
) {
    var name by remember { mutableStateOf(original?.name ?: "") }
    var price by remember { mutableStateOf(if (original != null) original.price.toString() else "") }
    var cycleDays by remember { mutableStateOf((original?.cycleInDays ?: 30).toString()) }
    var nextDue by remember { mutableStateOf(original?.nextDueDate ?: Calendar.getInstance().timeInMillis + 24 * 60 * 60 * 1000) }
    var icon by remember { mutableStateOf(original?.iconKey ?: "🎯") }

    Scaffold(
        topBar = {
            SmallTopAppBar(
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
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),

            )
            OutlinedTextField(
                value = price,
                onValueChange = { price = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Price") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = cycleDays,
                onValueChange = { cycleDays = it.filter { c -> c.isDigit() } },
                label = { Text("Cycle (days)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = icon,
                onValueChange = { icon = it.take(2) },
                label = { Text("Icon (emoji or 1-2 chars)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = nextDue.toString(),
                onValueChange = { new -> new.toLongOrNull()?.let { nextDue = it } },
                label = { Text("Next Due (epoch millis)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    val sub = Subscription(
                        id = original?.id ?: "",
                        name = name.trim(),
                        price = price.toDoubleOrNull() ?: 0.0,
                        cycleInDays = cycleDays.toIntOrNull() ?: 30,
                        nextDueDate = nextDue,
                        iconKey = icon.ifBlank { "📦" }
                    )
                    onSave(sub)
                }) { Text("Save") }
                OutlinedButton(onClick = onCancel) { Text("Cancel") }
            }
        }
    }
}