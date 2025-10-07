package com.example.todox.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todox.data.model.Todo
import com.example.todox.data.repo.SettingsRepository
import com.example.todox.data.repo.TodoRepository
import com.example.todox.workers.DueReminderScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TodoUiState(
    val today: List<Todo> = emptyList(),
    val all: List<Todo> = emptyList(),
    val completed: List<Todo> = emptyList(),
    val resetHour: Int = SettingsRepository.DEFAULT_RESET_HOUR,
    val isLoading: Boolean = false
)

class TodoViewModel(
    private val todoRepository: TodoRepository,
    private val settingsRepository: SettingsRepository,
    private val reminderScheduler: DueReminderScheduler
) : ViewModel() {

    private val resetHourState: StateFlow<Int> = settingsRepository.resetHour
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SettingsRepository.DEFAULT_RESET_HOUR
        )

    val uiState: StateFlow<TodoUiState> = combine(
        todoRepository.today(resetHourState),
        todoRepository.all,
        todoRepository.completed,
        resetHourState
    ) { today, all, completed, resetHour ->
        TodoUiState(
            today = today,
            all = all,
            completed = completed,
            resetHour = resetHour,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TodoUiState(isLoading = true)
    )

    fun todo(todoId: String): Flow<Todo?> = todoRepository.observe(todoId)

    fun addOrUpdate(todo: Todo) {
        viewModelScope.launch {
            val saved = todoRepository.upsert(todo)
            if (!saved.done && saved.dueAt != null) {
                reminderScheduler.schedule(saved)
            } else {
                reminderScheduler.cancel(saved.id)
            }
        }
    }

    fun delete(todoId: String) {
        viewModelScope.launch {
            todoRepository.delete(todoId)
            reminderScheduler.cancel(todoId)
        }
    }

    fun toggleDone(todoId: String, done: Boolean) {
        viewModelScope.launch {
            todoRepository.toggleDone(todoId, done)
            if (done) {
                reminderScheduler.cancel(todoId)
            } else {
                todoRepository.get(todoId)?.let { todo ->
                    if (todo.dueAt != null && !todo.done) {
                        reminderScheduler.schedule(todo)
                    }
                }
            }
        }
    }

    fun refreshReminder(todoId: String) {
        viewModelScope.launch {
            todoRepository.get(todoId)?.let { todo ->
                if (todo.dueAt != null && !todo.done) {
                    reminderScheduler.schedule(todo)
                } else {
                    reminderScheduler.cancel(todo.id)
                }
            }
        }
    }
}
