package com.example.todox.`data`.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.example.todox.`data`.db.Converters
import com.example.todox.`data`.model.Priority
import com.example.todox.`data`.model.Todo
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.IllegalArgumentException
import kotlin.Int
import kotlin.Lazy
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class TodoDao_Impl(
  __db: RoomDatabase,
) : TodoDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfTodo: EntityUpsertAdapter<Todo>

  private val __converters: Lazy<Converters> = lazy {
    checkNotNull(__db.getTypeConverter(Converters::class))
  }
  init {
    this.__db = __db
    this.__upsertAdapterOfTodo = EntityUpsertAdapter<Todo>(object : EntityInsertAdapter<Todo>() {
      protected override fun createQuery(): String =
          "INSERT INTO `todos` (`id`,`title`,`note`,`dueAt`,`daily`,`done`,`priority`,`tags`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Todo) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.title)
        val _tmpNote: String? = entity.note
        if (_tmpNote == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpNote)
        }
        val _tmpDueAt: Long? = entity.dueAt
        if (_tmpDueAt == null) {
          statement.bindNull(4)
        } else {
          statement.bindLong(4, _tmpDueAt)
        }
        val _tmp: Int = if (entity.daily) 1 else 0
        statement.bindLong(5, _tmp.toLong())
        val _tmp_1: Int = if (entity.done) 1 else 0
        statement.bindLong(6, _tmp_1.toLong())
        statement.bindText(7, __Priority_enumToString(entity.priority))
        val _tmp_2: String = __converters().fromTags(entity.tags)
        statement.bindText(8, _tmp_2)
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.updatedAt)
      }
    }, object : EntityDeleteOrUpdateAdapter<Todo>() {
      protected override fun createQuery(): String =
          "UPDATE `todos` SET `id` = ?,`title` = ?,`note` = ?,`dueAt` = ?,`daily` = ?,`done` = ?,`priority` = ?,`tags` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Todo) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.title)
        val _tmpNote: String? = entity.note
        if (_tmpNote == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpNote)
        }
        val _tmpDueAt: Long? = entity.dueAt
        if (_tmpDueAt == null) {
          statement.bindNull(4)
        } else {
          statement.bindLong(4, _tmpDueAt)
        }
        val _tmp: Int = if (entity.daily) 1 else 0
        statement.bindLong(5, _tmp.toLong())
        val _tmp_1: Int = if (entity.done) 1 else 0
        statement.bindLong(6, _tmp_1.toLong())
        statement.bindText(7, __Priority_enumToString(entity.priority))
        val _tmp_2: String = __converters().fromTags(entity.tags)
        statement.bindText(8, _tmp_2)
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.updatedAt)
        statement.bindText(11, entity.id)
      }
    })
  }

  public override suspend fun upsert(todo: Todo): Unit = performSuspending(__db, false, true) {
      _connection ->
    __upsertAdapterOfTodo.upsert(_connection, todo)
  }

  public override fun all(): Flow<List<Todo>> {
    val _sql: String = """
        |
        |        SELECT * FROM todos
        |        ORDER BY done ASC,
        |                 CASE priority WHEN 'HIGH' THEN 2 WHEN 'MID' THEN 1 ELSE 0 END DESC,
        |                 COALESCE(dueAt, 9223372036854775807),
        |                 createdAt DESC
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("todos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfDaily: Int = getColumnIndexOrThrow(_stmt, "daily")
        val _columnIndexOfDone: Int = getColumnIndexOrThrow(_stmt, "done")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<Todo> = mutableListOf()
        while (_stmt.step()) {
          val _item: Todo
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpDueAt: Long?
          if (_stmt.isNull(_columnIndexOfDueAt)) {
            _tmpDueAt = null
          } else {
            _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          }
          val _tmpDaily: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDaily).toInt()
          _tmpDaily = _tmp != 0
          val _tmpDone: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDone).toInt()
          _tmpDone = _tmp_1 != 0
          val _tmpPriority: Priority
          _tmpPriority = __Priority_stringToEnum(_stmt.getText(_columnIndexOfPriority))
          val _tmpTags: List<String>
          val _tmp_2: String
          _tmp_2 = _stmt.getText(_columnIndexOfTags)
          _tmpTags = __converters().toTags(_tmp_2)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              Todo(_tmpId,_tmpTitle,_tmpNote,_tmpDueAt,_tmpDaily,_tmpDone,_tmpPriority,_tmpTags,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun today(resetHour: Int): Flow<List<Todo>> {
    val _sql: String = """
        |
        |        WITH now_seconds AS (
        |            SELECT CAST(strftime('%s','now', 'utc') AS INTEGER) AS now_sec
        |        ),
        |        day_start AS (
        |            SELECT ((((now_sec - (? * 3600)) / 86400) * 86400) + (? * 3600)) * 1000 AS start_ms
        |            FROM now_seconds
        |        ),
        |        next_day_start AS (
        |            SELECT start_ms + 86400000 AS end_ms FROM day_start
        |        )
        |        SELECT * FROM todos
        |        WHERE daily = 1
        |           OR (
        |                dueAt IS NOT NULL
        |            AND dueAt >= (SELECT start_ms FROM day_start)
        |            AND dueAt < (SELECT end_ms FROM next_day_start)
        |           )
        |        ORDER BY done ASC,
        |                 CASE priority WHEN 'HIGH' THEN 2 WHEN 'MID' THEN 1 ELSE 0 END DESC,
        |                 COALESCE(dueAt, 9223372036854775807),
        |                 createdAt DESC
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("todos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, resetHour.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, resetHour.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfDaily: Int = getColumnIndexOrThrow(_stmt, "daily")
        val _columnIndexOfDone: Int = getColumnIndexOrThrow(_stmt, "done")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<Todo> = mutableListOf()
        while (_stmt.step()) {
          val _item: Todo
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpDueAt: Long?
          if (_stmt.isNull(_columnIndexOfDueAt)) {
            _tmpDueAt = null
          } else {
            _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          }
          val _tmpDaily: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDaily).toInt()
          _tmpDaily = _tmp != 0
          val _tmpDone: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDone).toInt()
          _tmpDone = _tmp_1 != 0
          val _tmpPriority: Priority
          _tmpPriority = __Priority_stringToEnum(_stmt.getText(_columnIndexOfPriority))
          val _tmpTags: List<String>
          val _tmp_2: String
          _tmp_2 = _stmt.getText(_columnIndexOfTags)
          _tmpTags = __converters().toTags(_tmp_2)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              Todo(_tmpId,_tmpTitle,_tmpNote,_tmpDueAt,_tmpDaily,_tmpDone,_tmpPriority,_tmpTags,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun completed(): Flow<List<Todo>> {
    val _sql: String = """
        |
        |        SELECT * FROM todos
        |        WHERE done = 1
        |        ORDER BY done ASC,
        |                 CASE priority WHEN 'HIGH' THEN 2 WHEN 'MID' THEN 1 ELSE 0 END DESC,
        |                 COALESCE(dueAt, 9223372036854775807),
        |                 createdAt DESC
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("todos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfDaily: Int = getColumnIndexOrThrow(_stmt, "daily")
        val _columnIndexOfDone: Int = getColumnIndexOrThrow(_stmt, "done")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<Todo> = mutableListOf()
        while (_stmt.step()) {
          val _item: Todo
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpDueAt: Long?
          if (_stmt.isNull(_columnIndexOfDueAt)) {
            _tmpDueAt = null
          } else {
            _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          }
          val _tmpDaily: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDaily).toInt()
          _tmpDaily = _tmp != 0
          val _tmpDone: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDone).toInt()
          _tmpDone = _tmp_1 != 0
          val _tmpPriority: Priority
          _tmpPriority = __Priority_stringToEnum(_stmt.getText(_columnIndexOfPriority))
          val _tmpTags: List<String>
          val _tmp_2: String
          _tmp_2 = _stmt.getText(_columnIndexOfTags)
          _tmpTags = __converters().toTags(_tmp_2)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              Todo(_tmpId,_tmpTitle,_tmpNote,_tmpDueAt,_tmpDaily,_tmpDone,_tmpPriority,_tmpTags,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeById(id: String): Flow<Todo?> {
    val _sql: String = "SELECT * FROM todos WHERE id = ?"
    return createFlow(__db, false, arrayOf("todos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfDaily: Int = getColumnIndexOrThrow(_stmt, "daily")
        val _columnIndexOfDone: Int = getColumnIndexOrThrow(_stmt, "done")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: Todo?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpDueAt: Long?
          if (_stmt.isNull(_columnIndexOfDueAt)) {
            _tmpDueAt = null
          } else {
            _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          }
          val _tmpDaily: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDaily).toInt()
          _tmpDaily = _tmp != 0
          val _tmpDone: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDone).toInt()
          _tmpDone = _tmp_1 != 0
          val _tmpPriority: Priority
          _tmpPriority = __Priority_stringToEnum(_stmt.getText(_columnIndexOfPriority))
          val _tmpTags: List<String>
          val _tmp_2: String
          _tmp_2 = _stmt.getText(_columnIndexOfTags)
          _tmpTags = __converters().toTags(_tmp_2)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              Todo(_tmpId,_tmpTitle,_tmpNote,_tmpDueAt,_tmpDaily,_tmpDone,_tmpPriority,_tmpTags,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: String): Todo? {
    val _sql: String = "SELECT * FROM todos WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfDaily: Int = getColumnIndexOrThrow(_stmt, "daily")
        val _columnIndexOfDone: Int = getColumnIndexOrThrow(_stmt, "done")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: Todo?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpDueAt: Long?
          if (_stmt.isNull(_columnIndexOfDueAt)) {
            _tmpDueAt = null
          } else {
            _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          }
          val _tmpDaily: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDaily).toInt()
          _tmpDaily = _tmp != 0
          val _tmpDone: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDone).toInt()
          _tmpDone = _tmp_1 != 0
          val _tmpPriority: Priority
          _tmpPriority = __Priority_stringToEnum(_stmt.getText(_columnIndexOfPriority))
          val _tmpTags: List<String>
          val _tmp_2: String
          _tmp_2 = _stmt.getText(_columnIndexOfTags)
          _tmpTags = __converters().toTags(_tmp_2)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              Todo(_tmpId,_tmpTitle,_tmpNote,_tmpDueAt,_tmpDaily,_tmpDone,_tmpPriority,_tmpTags,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun dueAfter(nowMillis: Long): List<Todo> {
    val _sql: String = "SELECT * FROM todos WHERE dueAt IS NOT NULL AND dueAt > ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, nowMillis)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfDaily: Int = getColumnIndexOrThrow(_stmt, "daily")
        val _columnIndexOfDone: Int = getColumnIndexOrThrow(_stmt, "done")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<Todo> = mutableListOf()
        while (_stmt.step()) {
          val _item: Todo
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpDueAt: Long?
          if (_stmt.isNull(_columnIndexOfDueAt)) {
            _tmpDueAt = null
          } else {
            _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          }
          val _tmpDaily: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDaily).toInt()
          _tmpDaily = _tmp != 0
          val _tmpDone: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDone).toInt()
          _tmpDone = _tmp_1 != 0
          val _tmpPriority: Priority
          _tmpPriority = __Priority_stringToEnum(_stmt.getText(_columnIndexOfPriority))
          val _tmpTags: List<String>
          val _tmp_2: String
          _tmp_2 = _stmt.getText(_columnIndexOfTags)
          _tmpTags = __converters().toTags(_tmp_2)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              Todo(_tmpId,_tmpTitle,_tmpNote,_tmpDueAt,_tmpDaily,_tmpDone,_tmpPriority,_tmpTags,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun delete(id: String) {
    val _sql: String = "DELETE FROM todos WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun toggleDone(
    id: String,
    done: Boolean,
    updatedAt: Long,
  ) {
    val _sql: String = "UPDATE todos SET done = ?, updatedAt = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (done) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun resetDaily(updatedAt: Long) {
    val _sql: String = "UPDATE todos SET done = 0, updatedAt = ? WHERE daily = 1 AND done = 1"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, updatedAt)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  private fun __Priority_enumToString(_value: Priority): String = when (_value) {
    Priority.LOW -> "LOW"
    Priority.MID -> "MID"
    Priority.HIGH -> "HIGH"
  }

  private fun __converters(): Converters = __converters.value

  private fun __Priority_stringToEnum(_value: String): Priority = when (_value) {
    "LOW" -> Priority.LOW
    "MID" -> Priority.MID
    "HIGH" -> Priority.HIGH
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = listOf(Converters::class)
  }
}
