package com.example.todox.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class Priority {
    LOW, MID, HIGH
}

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val note: String? = null,
    val dueAt: Long? = null,
    val daily: Boolean = false,
    val done: Boolean = false,
    val priority: Priority = Priority.MID,
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
