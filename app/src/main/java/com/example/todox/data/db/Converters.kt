package com.example.todox.data.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

@ProvidedTypeConverter
class Converters(
    private val json: Json
) {
    @TypeConverter
    fun fromTags(tags: List<String>): String =
        if (tags.isEmpty()) {
            "[]"
        } else {
            buildJsonArray {
                tags.forEach { add(JsonPrimitive(it)) }
            }.toString()
        }

    @TypeConverter
    fun toTags(raw: String): List<String> =
        if (raw.isBlank()) {
            emptyList()
        } else {
            json.parseToJsonElement(raw).jsonArray.map { it.jsonPrimitive.content }
        }
}
