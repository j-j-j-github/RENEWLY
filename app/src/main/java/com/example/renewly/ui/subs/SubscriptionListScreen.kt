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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.renewly.data.Subscription
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class) // ADD THIS LINE
@Composable
fun SubscriptionListScreen(
    subs: List<Subscription>,
    dark: Boolean,
    onToggleDark: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (Subscription) -> Unit,
    onDelete: (Subscription) -> Unit,
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Renewly") },
                actions = {
                    IconButton(onClick = onToggleDark) { Icon(Icons.Default.Brightness6, contentDescription = null) }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) { Icon(Icons.Default.Add, contentDescription = null) }
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
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)
                        ))
                    ), contentAlignment = Alignment.Center
            ) {
                Text(sub.iconKey.take(2), style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(sub.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("₹" + String.format("%.2f", sub.price))
                CountdownText(targetMillis = sub.nextDueDate)
            }

        }
    }
}

@Composable
private fun CountdownText(targetMillis: Long) {
    var now by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(targetMillis) {
        while (true) {
            now = System.currentTimeMillis()
            delay(1000)
        }
    }
    val diff = (targetMillis - now).coerceAtLeast(0)
    val d = TimeUnit.MILLISECONDS.toDays(diff)
    val h = TimeUnit.MILLISECONDS.toHours(diff - TimeUnit.DAYS.toMillis(d))
    val m = TimeUnit.MILLISECONDS.toMinutes(diff - TimeUnit.DAYS.toMillis(d) - TimeUnit.HOURS.toMillis(h))
    val s = TimeUnit.MILLISECONDS.toSeconds(diff - TimeUnit.DAYS.toMillis(d) - TimeUnit.HOURS.toMillis(h) - TimeUnit.MINUTES.toMillis(m))
    val overdue = targetMillis < now

    Text(
        if (!overdue) "$d d $h h $m m $s s left" else "Due!",
        color = if (overdue) Color.Red else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
        style = MaterialTheme.typography.titleMedium // This makes the text bigger
    )
}