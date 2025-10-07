package com.example.todox.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.todox.R
import com.example.todox.data.model.Todo
import com.example.todox.ui.list.TodoListScreen
import com.example.todox.vm.TodoUiState

enum class TodoTab(@StringRes val labelRes: Int) {
    TODAY(R.string.tab_today),
    ALL(R.string.tab_all),
    DONE(R.string.tab_done);

    companion object {
        fun fromRoute(route: String?): TodoTab = when (route?.lowercase()) {
            "all" -> ALL
            "done", "completed" -> DONE
            else -> TODAY
        }

        fun toRoute(tab: TodoTab): String = when (tab) {
            TODAY -> "today"
            ALL -> "all"
            DONE -> "done"
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    uiState: TodoUiState,
    selectedTab: TodoTab,
    onTabSelected: (TodoTab) -> Unit,
    onToggleDone: (Todo, Boolean) -> Unit,
    onDelete: (Todo) -> Unit,
    onEdit: (String?) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val todos = when (selectedTab) {
        TodoTab.TODAY -> uiState.today
        TodoTab.ALL -> uiState.all
        TodoTab.DONE -> uiState.completed
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(id = R.string.menu_settings)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onEdit(null) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.fab_add)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab.ordinal) {
                TodoTab.entries.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab.ordinal == index,
                        onClick = { onTabSelected(tab) },
                        text = { Text(text = stringResource(id = tab.labelRes)) }
                    )
                }
            }

            TodoListScreen(
                todos = todos,
                onToggleDone = { todo -> onToggleDone(todo, !todo.done) },
                onDelete = onDelete,
                onEdit = { todo -> onEdit(todo.id) },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
