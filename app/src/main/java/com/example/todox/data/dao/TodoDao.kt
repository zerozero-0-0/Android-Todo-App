package com.example.todox.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.todox.data.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Upsert
    suspend fun upsert(todo: Todo)

    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun delete(id: String)

    @Query("UPDATE todos SET done = :done, updatedAt = :updatedAt WHERE id = :id")
    suspend fun toggleDone(id: String, done: Boolean, updatedAt: Long)

    @Query(
        """
        SELECT * FROM todos
        ORDER BY done ASC,
                 CASE priority WHEN 'HIGH' THEN 2 WHEN 'MID' THEN 1 ELSE 0 END DESC,
                 COALESCE(dueAt, 9223372036854775807),
                 createdAt DESC
        """
    )
    fun all(): Flow<List<Todo>>

    @Query(
        """
        WITH now_seconds AS (
            SELECT CAST(strftime('%s','now', 'utc') AS INTEGER) AS now_sec
        ),
        day_start AS (
            SELECT ((((now_sec - (:resetHour * 3600)) / 86400) * 86400) + (:resetHour * 3600)) * 1000 AS start_ms
            FROM now_seconds
        ),
        next_day_start AS (
            SELECT start_ms + 86400000 AS end_ms FROM day_start
        )
        SELECT * FROM todos
        WHERE daily = 1
           OR (
                dueAt IS NOT NULL
            AND dueAt >= (SELECT start_ms FROM day_start)
            AND dueAt < (SELECT end_ms FROM next_day_start)
           )
        ORDER BY done ASC,
                 CASE priority WHEN 'HIGH' THEN 2 WHEN 'MID' THEN 1 ELSE 0 END DESC,
                 COALESCE(dueAt, 9223372036854775807),
                 createdAt DESC
        """
    )
    fun today(resetHour: Int): Flow<List<Todo>>

    @Query(
        """
        SELECT * FROM todos
        WHERE done = 1
        ORDER BY done ASC,
                 CASE priority WHEN 'HIGH' THEN 2 WHEN 'MID' THEN 1 ELSE 0 END DESC,
                 COALESCE(dueAt, 9223372036854775807),
                 createdAt DESC
        """
    )
    fun completed(): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE id = :id")
    fun observeById(id: String): Flow<Todo?>

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getById(id: String): Todo?

    @Query("SELECT * FROM todos WHERE dueAt IS NOT NULL AND dueAt > :nowMillis")
    suspend fun dueAfter(nowMillis: Long): List<Todo>

    @Query("UPDATE todos SET done = 0, updatedAt = :updatedAt WHERE daily = 1 AND done = 1")
    suspend fun resetDaily(updatedAt: Long)
}
