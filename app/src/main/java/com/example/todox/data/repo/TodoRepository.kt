package com.example.todox.data.repo

import com.example.todox.data.dao.TodoDao
import com.example.todox.data.model.Todo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.withContext

class TodoRepository(
    private val dao: TodoDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    val all: Flow<List<Todo>> = dao.all()

    fun today(resetHourFlow: Flow<Int>): Flow<List<Todo>> =
        resetHourFlow.flatMapLatest { hour -> dao.today(hour) }

    val completed: Flow<List<Todo>> = dao.completed()

    fun observe(id: String): Flow<Todo?> = dao.observeById(id)

    suspend fun upsert(todo: Todo): Todo = withContext(ioDispatcher) {
        val timestamp = System.currentTimeMillis()
        val normalized = todo.copy(
            tags = todo.tags.mapNotNull { it.trim().ifBlank { null } },
            updatedAt = timestamp
        )
        dao.upsert(normalized)
        normalized
    }

    suspend fun delete(id: String) = withContext(ioDispatcher) {
        dao.delete(id)
    }

    suspend fun toggleDone(id: String, done: Boolean) = withContext(ioDispatcher) {
        dao.toggleDone(id, done, System.currentTimeMillis())
    }

    suspend fun resetDailyTodos() = withContext(ioDispatcher) {
        dao.resetDaily(System.currentTimeMillis())
    }

    suspend fun get(id: String): Todo? = withContext(ioDispatcher) {
        dao.getById(id)
    }

    suspend fun dueAfter(now: Long): List<Todo> = withContext(ioDispatcher) {
        dao.dueAfter(now)
    }
}
