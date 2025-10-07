package com.example.todox.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.todox.R
import com.example.todox.data.model.Priority
import com.example.todox.data.model.Todo
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    todos: List<Todo>,
    onToggleDone: (Todo) -> Unit,
    onDelete: (Todo) -> Unit,
    onEdit: (Todo) -> Unit,
    modifier: Modifier = Modifier
) {
    if (todos.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.empty_state_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        items(
            items = todos,
            key = { it.id }
        ) { todo ->
            TodoDismissItem(
                todo = todo,
                onToggleDone = onToggleDone,
                onDelete = onDelete,
                onEdit = onEdit
            )
            Divider()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoDismissItem(
    todo: Todo,
    onToggleDone: (Todo) -> Unit,
    onDelete: (Todo) -> Unit,
    onEdit: (Todo) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete(todo)
                true
            } else {
                value != SwipeToDismissBoxValue.StartToEnd
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            ) {}
        },
        content = {
            TodoRow(
                todo = todo,
                onToggleDone = { onToggleDone(todo) },
                onEdit = { onEdit(todo) }
            )
        }
    )
}

@Composable
private fun TodoRow(
    todo: Todo,
    onToggleDone: () -> Unit,
    onEdit: () -> Unit
) {
    val formatter = remember { DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT) }
    val dueText = remember(todo.dueAt) {
        todo.dueAt?.let { due ->
            Instant.ofEpochMilli(due)
                .atZone(ZoneId.systemDefault())
                .let(formatter::format)
        }
    }
    val tagsText = remember(todo.tags) {
        todo.tags.takeIf { it.isNotEmpty() }?.joinToString(", ")
    }

    val alpha = if (todo.done) 0.6f else 1f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleDone() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = todo.done,
            onCheckedChange = { onToggleDone() }
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .alpha(alpha)
        ) {
            Text(
                text = todo.title,
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (todo.done) TextDecoration.LineThrough else null
            )
            if (!todo.note.isNullOrBlank()) {
                Text(
                    text = todo.note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (dueText != null) {
                Text(
                    text = dueText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                val priorityLabel = stringResource(
                    when (todo.priority) {
                        Priority.HIGH -> R.string.priority_high
                        Priority.MID -> R.string.priority_mid
                        Priority.LOW -> R.string.priority_low
                    }
                )
                AssistChip(
                    onClick = {},
                    label = {
                        Text(text = priorityLabel)
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
                if (todo.daily) {
                    AssistChip(
                        onClick = {},
                        label = { Text(text = stringResource(id = R.string.edit_daily)) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    )
                }
            }
            if (tagsText != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tagsText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        IconButton(
            onClick = onEdit
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(id = R.string.edit_todo_content_description)
            )
        }
    }
}
