package com.example.todox

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todox.data.dao.TodoDao
import com.example.todox.data.db.AppDatabase
import com.example.todox.data.db.Converters
import com.example.todox.data.model.Priority
import com.example.todox.data.model.Todo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodoDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: TodoDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val json = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
        db = androidx.room.Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .addTypeConverter(Converters(json))
            .allowMainThreadQueries()
            .build()
        dao = db.todoDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun upsertAndQueryAll() = runBlocking {
        val todo = Todo(
            title = "テスト",
            priority = Priority.HIGH,
            tags = listOf("home", "urgent")
        )

        dao.upsert(todo)
        val list = dao.all().first()

        assertEquals(1, list.size)
        assertEquals(todo.id, list.first().id)
        assertEquals(Priority.HIGH, list.first().priority)
    }

    @Test
    fun todayIncludesDailyTodos() = runBlocking {
        val todo = Todo(
            title = "毎日確認",
            daily = true,
            done = true
        )
        dao.upsert(todo)

        dao.resetDaily(System.currentTimeMillis())
        val today = dao.today(0).first()

        assertEquals(1, today.size)
        assertTrue(today.first().daily)
        assertEquals(false, today.first().done)
    }
}
