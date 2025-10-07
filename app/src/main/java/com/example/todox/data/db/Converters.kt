package com.example.todox.data.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@ProvidedTypeConverter
class Converters(
    private val json: Json
) {
    private val serializer = ListSerializer(String.serializer())

    @TypeConverter
    fun fromTags(tags: List<String>): String = json.encodeToString(serializer, tags)

    @TypeConverter
    fun toTags(raw: String): List<String> =
        if (raw.isBlank()) {
            emptyList()
        } else {
            json.decodeFromString(serializer, raw)
        }
}
