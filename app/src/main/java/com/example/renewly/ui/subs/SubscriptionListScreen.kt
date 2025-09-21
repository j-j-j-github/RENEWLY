package com.example.renewly.ui.subs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.renewly.data.CycleType
import com.example.renewly.data.Subscription
import com.example.renewly.ui.auth.EditNameDialog
import com.example.renewly.ui.auth.ProfileViewModel
import com.example.renewly.ui.theme.AppGradients
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    onLogout: () -> Unit,
    onNavigateToAuth: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    var showEditNameDialog by remember { mutableStateOf(false) }

    val profileViewModel: ProfileViewModel = viewModel()
    val uiState by profileViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Launcher for picking an image from the device
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                // Trigger the profile picture upload in the ViewModel
                profileViewModel.uploadProfilePicture(context, uri)
            }
        }
    }

    if (showEditNameDialog) {
        EditNameDialog(
            currentName = uiState.name,
            onDismiss = { showEditNameDialog = false },
            onSave = { newName ->
                profileViewModel.updateUserName(newName, context)
            }
        )
    }

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

        if (showMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showMenu = false }
            )
            Surface(
                color = Color.Black,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(220.dp)
                    .align(Alignment.CenterEnd)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile picture and details
                    Spacer(Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.DarkGray)
                            .clickable { launcher.launch("image/*") }, // Clickable to change photo
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.photoUrl.isNullOrEmpty()) {
                            Text(
                                text = uiState.name.take(1).uppercase(),
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White
                            )
                        } else {
                            AsyncImage(
                                model = uiState.photoUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        // Optional: Add an overlay icon for editing
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color.Gray.copy(alpha = 0.7f))
                                .align(Alignment.BottomEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change Profile Picture",
                                tint = Color.White,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    if (uiState.isLoggedIn) {
                        Text(
                            text = uiState.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = uiState.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                        Spacer(Modifier.height(8.dp))
                        TextButton(
                            onClick = {
                                profileViewModel.sendPasswordReset(context)
                            }
                        ) {
                            Text("Reset Password", color = Color.LightGray)
                        }
                        Spacer(Modifier.weight(1f))
                        Button(
                            onClick = { onLogout(); showMenu = false },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                        ) {
                            Text("Logout")
                        }
                    } else {
                        Text(
                            "You are not logged in.",
                            color = Color.White
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { onNavigateToAuth(); showMenu = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Login / Sign Up")
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun SubscriptionCard(sub: Subscription, onClick: () -> Unit, onDelete: () -> Unit) {
// --- THIS IS THE CHANGE ---
// 1. Get the selected gradient brush, with a default fallback
    val cardBrush = AppGradients.getBrushByHex(sub.colorHex)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
// 2. We remove the static containerColor to let the gradient shine through
    ) {
// 3. Apply the gradient to the background of the Row
        Row(
            Modifier
                .background(cardBrush)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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