package com.example.renewly
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.renewly.data.Subscription
import com.example.renewly.data.ThemeRepository
import com.example.renewly.ui.auth.AuthScreen
import com.example.renewly.ui.subs.AddEditSubscriptionScreen
import com.example.renewly.ui.subs.SubscriptionListScreen
import com.example.renewly.ui.subs.SubscriptionsViewModel
import com.example.renewly.ui.theme.RenewlyTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val auth = remember { FirebaseAuth.getInstance() }
            val themeRepo = remember { ThemeRepository(this) }
            var dark by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()

            // Observe saved theme
            LaunchedEffect(Unit) {
                themeRepo.darkFlow.collectLatest { dark = it }
            }

            RenewlyTheme(darkTheme = dark) {
                val nav = rememberNavController()
                val authed = auth.currentUser != null

                NavHost(
                    navController = nav,
                    startDestination = if (authed) "list" else "auth"
                ) {
                    // --- Auth screen ---
                    composable("auth") {
                        AuthScreen(
                            onAuthed = {
                                nav.navigate("list") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        )
                    }

                    // --- Subscription list ---
                    composable("list") {
                        val vm: SubscriptionsViewModel = viewModel()
                        SubscriptionListScreen(
                            subs = vm.subs.collectAsState().value,
                            dark = dark,
                            onToggleDark = { scope.launch { themeRepo.setDark(!dark) } },
                            onAdd = { nav.navigate("edit") },
                            onEdit = { sub -> nav.navigate("edit?id=${sub.id}") },
                            onDelete = { sub -> vm.delete(sub.id) },
                            onLogout = {
                                auth.signOut()
                                nav.navigate("auth") {
                                    popUpTo("list") { inclusive = true }
                                }
                            },
                        )
                    }

                    // --- Add new subscription ---
                    composable("edit") {
                        val vm: SubscriptionsViewModel = viewModel()
                        AddEditSubscriptionScreen(
                            original = null,
                            onSave = { sub ->
                                vm.add(sub)
                                nav.popBackStack()
                            },
                            onCancel = { nav.popBackStack() },
                            onDelete = {}
                        )
                    }

                    // --- Edit existing subscription ---
                    composable(
                        route = "edit?id={id}",
                        arguments = listOf(navArgument("id") {
                            type = NavType.StringType
                            defaultValue = ""
                        })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id").orEmpty()
                        val vm: SubscriptionsViewModel = viewModel()
                        val existing = vm.subs.collectAsState().value.find { it.id == id }

                        AddEditSubscriptionScreen(
                            original = existing,
                            onSave = { sub ->
                                if (existing == null) vm.add(sub) else vm.update(existing.id, sub)
                                nav.popBackStack()
                            },
                            onCancel = { nav.popBackStack() },
                            onDelete = { sub ->
                                vm.delete(sub.id)
                                nav.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}