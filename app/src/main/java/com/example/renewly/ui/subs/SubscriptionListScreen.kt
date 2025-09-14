package com.example.renewly.ui.subs

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.renewly.data.CycleType
import com.example.renewly.data.Subscription
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionListScreen(
    subs: List<Subscription>,
    dark: Boolean,
    onToggleDark: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (Subscription) -> Unit,
    onDelete: (Subscription) -> Unit,
    onLogout: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("RENEWLY") },
                    actions = {
                        IconButton(onClick = onToggleDark) {
                            Icon(Icons.Default.Brightness6, contentDescription = "Toggle Theme")
                        }
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onAdd) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        ) { padding ->
            LazyColumn(
                contentPadding = padding,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(subs) { sub ->
                    SubscriptionCard(
                        sub = sub,
                        onClick = { onEdit(sub) },
                        onDelete = { onDelete(sub) }
                    )
                }
            }
        }

        // Side menu
        if (showMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showMenu = false }
            )

            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(220.dp)
                    .align(Alignment.CenterEnd)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text("Menu", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { onLogout(); showMenu = false }) {
                        Text("Logout")
                    }
                }
            }
        }
    }
}

@Composable
private fun SubscriptionCard(sub: Subscription, onClick: () -> Unit, onDelete: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        )
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // ✅ If iconUri is a real Supabase URL, show image. Else fallback to emoji/char.
                if (!sub.iconUri.isNullOrEmpty() && sub.iconUri!!.startsWith("http")) {
                    AsyncImage(
                        model = sub.iconUri,
                        contentDescription = sub.name,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(sub.iconKey.take(2), style = MaterialTheme.typography.titleMedium)
                    }
                }

                Spacer(Modifier.width(12.dp))
                Column {
                    Text(sub.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("₹" + String.format("%.2f", sub.price))
                }
            }

            CountdownText(
                startMillis = sub.nextDueDate,
                cycleType = if (sub.cycleInDays >= 365) CycleType.YEARLY else CycleType.MONTHLY
            )
        }
    }
}

@Composable
private fun CountdownText(startMillis: Long, cycleType: CycleType) {
    val now by produceState(System.currentTimeMillis()) {
        while (true) {
            value = System.currentTimeMillis()
            delay(1000)
        }
    }

    val nextDue = remember(startMillis, cycleType, now) {
        val cal = Calendar.getInstance().apply { timeInMillis = startMillis }
        val current = Calendar.getInstance().apply { timeInMillis = now }

        // Roll until future
        while (!cal.after(current)) {
            when (cycleType) {
                CycleType.MONTHLY -> cal.add(Calendar.MONTH, 1)
                CycleType.YEARLY -> cal.add(Calendar.YEAR, 1)
            }
        }
        cal.timeInMillis
    }

    val diff = (nextDue - now).coerceAtLeast(0)
    val d = TimeUnit.MILLISECONDS.toDays(diff)
    val h = TimeUnit.MILLISECONDS.toHours(diff - TimeUnit.DAYS.toMillis(d))
    val dueNow = diff == 0L

    Text(
        if (dueNow) "Due!" else "$d Days, $h Hours",
        color = if (dueNow) Color.Red else MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}