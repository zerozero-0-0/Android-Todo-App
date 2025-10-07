package com.example.todox.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavType
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todox.App
import com.example.todox.data.model.Todo
import com.example.todox.ui.edit.EditScreen
import com.example.todox.ui.settings.SettingsScreen
import com.example.todox.ui.theme.TodoXTheme
import com.example.todox.vm.SettingsViewModel
import com.example.todox.vm.TodoViewModel
import kotlinx.coroutines.flow.flowOf

class MainActivity : ComponentActivity() {

    private val app: App by lazy { application as App }

    private val todoViewModel: TodoViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return TodoViewModel(
                        app.todoRepository,
                        app.settingsRepository,
                        app.dueReminderScheduler
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    private val settingsViewModel: SettingsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return SettingsViewModel(
                        app.settingsRepository,
                        app.dailyResetScheduler
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val launchTodoId = intent?.getStringExtra(EXTRA_TODO_ID)
        intent?.removeExtra(EXTRA_TODO_ID)

        setContent {
            TodoXTheme {
                val navController = rememberNavController()
                val uiState by todoViewModel.uiState.collectAsStateWithLifecycle()
                val resetHour by settingsViewModel.resetHour.collectAsStateWithLifecycle()

                var hasNotificationPermission by remember {
                    mutableStateOf(hasNotificationPermission())
                }

                val permissionLauncher = rememberLauncher(hasNotificationPermission = hasNotificationPermission) { granted ->
                    hasNotificationPermission = granted || hasNotificationPermission()
                }

                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            hasNotificationPermission = hasNotificationPermission()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                }

                LaunchedEffect(launchTodoId) {
                    if (!launchTodoId.isNullOrBlank()) {
                        navController.navigate(Routes.edit(launchTodoId)) {
                            launchSingleTop = true
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = Routes.TAB_ROUTE
                ) {
                    composable(
                        route = Routes.TAB_ROUTE,
                        arguments = listOf(
                            navArgument("tab") {
                                type = NavType.StringType
                                defaultValue = TodoTab.toRoute(TodoTab.TODAY)
                            }
                        )
                    ) { backStackEntry ->
                        val tab = TodoTab.fromRoute(backStackEntry.arguments?.getString("tab"))
                        MainScreen(
                            uiState = uiState,
                            selectedTab = tab,
                            onTabSelected = { selected ->
                                navController.navigate(Routes.tab(selected)) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            onToggleDone = { todo, nextDone ->
                                todoViewModel.toggleDone(todo.id, nextDone)
                            },
                            onDelete = { todo ->
                                todoViewModel.delete(todo.id)
                            },
                            onEdit = { id ->
                                navController.navigate(Routes.edit(id))
                            },
                            onOpenSettings = {
                                navController.navigate(Routes.SETTINGS) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable(
                        route = Routes.EDIT_ROUTE,
                        arguments = listOf(
                            navArgument("todoId") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            }
                        )
                    ) { backStackEntry ->
                        val todoId = backStackEntry.arguments?.getString("todoId")
                        val todoFlow = remember(todoId) {
                            if (todoId.isNullOrBlank()) flowOf<Todo?>(null)
                            else todoViewModel.todo(todoId)
                        }
                        val todo by todoFlow.collectAsStateWithLifecycle(initialValue = null)

                        EditScreen(
                            todo = todo,
                            onSave = { updated ->
                                todoViewModel.addOrUpdate(updated)
                                navController.popBackStack()
                            },
                            onDelete = if (todoId != null) {
                                {
                                    todoViewModel.delete(todoId)
                                    navController.popBackStack()
                                }
                            } else {
                                null
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Routes.SETTINGS) {
                        SettingsScreen(
                            resetHour = resetHour,
                            hasNotificationPermission = hasNotificationPermission,
                            onResetHourChange = { hour ->
                                settingsViewModel.updateResetHour(hour)
                            },
                            onRequestNotificationPermission = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissionLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(this).areNotificationsEnabled()
        }
    }

    companion object {
        const val EXTRA_TODO_ID = "extra.todo_id"
    }
}

private object Routes {
    private const val TAB_BASE = "tab"
    const val TAB_ROUTE = "$TAB_BASE/{tab}"
    private const val EDIT_BASE = "edit"
    const val EDIT_ROUTE = "$EDIT_BASE?todoId={todoId}"
    const val SETTINGS = "settings"

    fun tab(tab: TodoTab): String = "$TAB_BASE/${TodoTab.toRoute(tab)}"
    fun edit(todoId: String?): String = when {
        todoId.isNullOrBlank() -> EDIT_BASE
        else -> "$EDIT_BASE?todoId=$todoId"
    }
}

@Composable
private fun ComponentActivity.rememberLauncher(
    hasNotificationPermission: Boolean,
    onResult: (Boolean) -> Unit
): androidx.activity.result.ActivityResultLauncher<String>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
        androidx.activity.compose.rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = onResult
        )
    } else {
        null
    }
}
